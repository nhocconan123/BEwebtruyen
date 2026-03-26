package com.truyen.webtruyen.service;

import com.truyen.webtruyen.config.OtpProperties;
import com.truyen.webtruyen.dto.auth.MessageResponse;
import com.truyen.webtruyen.entity.EmailOtp;
import com.truyen.webtruyen.entity.User;
import com.truyen.webtruyen.entity.enums.OtpPurpose;
import com.truyen.webtruyen.repository.EmailOtpRepository;
import com.truyen.webtruyen.repository.UserRepository;
import com.truyen.webtruyen.util.OtpUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class PasswordOtpService {
    private static final String DEFAULT_RESET_PASSWORD = "123456";
    private static final String RESET_GENERIC_MESSAGE = "If the email exists, a new password has been sent.";

    private final UserRepository userRepository;
    private final EmailOtpRepository emailOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OtpProperties otpProperties;

    public PasswordOtpService(UserRepository userRepository,
                              EmailOtpRepository emailOtpRepository,
                              PasswordEncoder passwordEncoder,
                              EmailService emailService,
                              OtpProperties otpProperties) {
        this.userRepository = userRepository;
        this.emailOtpRepository = emailOtpRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.otpProperties = otpProperties;
    }

    @Transactional
    public MessageResponse resetPasswordByEmail(String emailRaw) {
        // Same response for all outcomes to avoid email enumeration.
        emailService.assertConfigured();

        String email = normalizeEmail(emailRaw);
        if (email == null) {
            return new MessageResponse(RESET_GENERIC_MESSAGE);
        }

        if (shouldThrottle(email, OtpPurpose.RESET_PASSWORD)) {
            return new MessageResponse(RESET_GENERIC_MESSAGE);
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return new MessageResponse(RESET_GENERIC_MESSAGE);
        }

        user.setPassword(passwordEncoder.encode(DEFAULT_RESET_PASSWORD));
        userRepository.save(user);

        // Persist a row so existing cooldown/hourly limits continue to work for reset flow.
        EmailOtp row = new EmailOtp();
        row.setUserId(user.getId());
        row.setEmail(email);
        row.setPurpose(OtpPurpose.RESET_PASSWORD);
        row.setOtpHash(OtpUtil.sha256Hex("RESET|" + email + "|" + System.nanoTime()));
        row.setExpiresAt(LocalDateTime.now().plusMinutes(otpProperties.getExpiryMinutes()));
        row.setConsumedAt(LocalDateTime.now());
        emailOtpRepository.save(row);

        emailService.sendHtml(
                email,
                "WebTruyen - Password reset",
                buildResetPasswordEmailHtml(DEFAULT_RESET_PASSWORD)
        );

        return new MessageResponse(RESET_GENERIC_MESSAGE);
    }

    @Transactional
    public MessageResponse sendResetPasswordOtp(String emailRaw) {
        // Fail fast in a non-enumerable way: same response regardless of email existence.
        emailService.assertConfigured();

        String email = normalizeEmail(emailRaw);
        if (email == null) {
            return new MessageResponse("If the email exists, an OTP has been sent.");
        }

        if (shouldThrottle(email, OtpPurpose.RESET_PASSWORD)) {
            return new MessageResponse("If the email exists, an OTP has been sent.");
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return new MessageResponse("If the email exists, an OTP has been sent.");
        }

        String otp = OtpUtil.generateNumericCode(otpProperties.getDigits());
        EmailOtp row = new EmailOtp();
        row.setUserId(user.getId());
        row.setEmail(email);
        row.setPurpose(OtpPurpose.RESET_PASSWORD);
        row.setOtpHash(hashOtp(email, OtpPurpose.RESET_PASSWORD, otp));
        row.setExpiresAt(LocalDateTime.now().plusMinutes(otpProperties.getExpiryMinutes()));
        emailOtpRepository.save(row);

        emailService.sendHtml(
                email,
                "WebTruyen OTP - Reset password",
                buildOtpEmailHtml(otp, otpProperties.getExpiryMinutes())
        );

        return new MessageResponse("If the email exists, an OTP has been sent.");
    }

    @Transactional
    public MessageResponse confirmResetPassword(String emailRaw, String otp, String newPassword) {
        String email = normalizeEmail(emailRaw);
        if (email == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid code or expired");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Invalid code or expired"));
        verifyAndConsumeOtp(user.getId(), email, OtpPurpose.RESET_PASSWORD, otp);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return new MessageResponse("Password updated");
    }

    @Transactional
    public MessageResponse sendChangePasswordOtp(User currentUser) {
        String email = normalizeEmail(currentUser.getEmail());
        if (email == null) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "User email is missing");
        }

        if (shouldThrottle(email, OtpPurpose.CHANGE_PASSWORD)) {
            return new MessageResponse("OTP sent");
        }

        String otp = OtpUtil.generateNumericCode(otpProperties.getDigits());
        EmailOtp row = new EmailOtp();
        row.setUserId(currentUser.getId());
        row.setEmail(email);
        row.setPurpose(OtpPurpose.CHANGE_PASSWORD);
        row.setOtpHash(hashOtp(email, OtpPurpose.CHANGE_PASSWORD, otp));
        row.setExpiresAt(LocalDateTime.now().plusMinutes(otpProperties.getExpiryMinutes()));
        emailOtpRepository.save(row);

        emailService.sendHtml(
                email,
                "WebTruyen OTP - Change password",
                buildOtpEmailHtml(otp, otpProperties.getExpiryMinutes())
        );

        return new MessageResponse("OTP sent");
    }

    @Transactional
    public MessageResponse confirmChangePassword(User currentUser, String currentPassword, String otp, String newPassword) {
        if (!matchesCurrentPassword(currentUser, currentPassword)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid credentials");
        }

        String email = normalizeEmail(currentUser.getEmail());
        if (email == null) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "User email is missing");
        }

        verifyAndConsumeOtp(currentUser.getId(), email, OtpPurpose.CHANGE_PASSWORD, otp);
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
        return new MessageResponse("Password updated");
    }

    @Transactional
    public MessageResponse changePassword(User currentUser, String currentPassword, String newPassword) {
        if (!matchesCurrentPassword(currentUser, currentPassword)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid credentials");
        }
        // Reject reusing the existing password (including legacy plain-text rows).
        if (matchesCurrentPassword(currentUser, newPassword)) {
            throw new ResponseStatusException(BAD_REQUEST, "New password must be different");
        }

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
        return new MessageResponse("Password updated");
    }

    private boolean matchesCurrentPassword(User user, String raw) {
        if (raw == null) {
            return false;
        }
        String stored = user.getPassword();
        if (stored == null) {
            return false;
        }
        // Backward-compatible with legacy plain text rows.
        return passwordEncoder.matches(raw, stored) || raw.equals(stored);
    }

    private void verifyAndConsumeOtp(Long expectedUserId, String email, OtpPurpose purpose, String otp) {
        EmailOtp row = emailOtpRepository.findTopByEmailAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(email, purpose)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Invalid code or expired"));

        if (row.getExpiresAt() == null || row.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid code or expired");
        }
        if (row.getUserId() != null && expectedUserId != null && !row.getUserId().equals(expectedUserId)) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid code or expired");
        }

        if (row.getAttemptCount() >= otpProperties.getMaxVerifyAttempts()) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid code or expired");
        }

        String expectedHash = row.getOtpHash();
        String actualHash = hashOtp(email, purpose, otp);
        if (!OtpUtil.constantTimeEquals(expectedHash, actualHash)) {
            row.setAttemptCount(row.getAttemptCount() + 1);
            emailOtpRepository.save(row);
            throw new ResponseStatusException(BAD_REQUEST, "Invalid code or expired");
        }

        row.setConsumedAt(LocalDateTime.now());
        emailOtpRepository.save(row);
    }

    private boolean shouldThrottle(String email, OtpPurpose purpose) {
        // Cooldown based on most recent row.
        EmailOtp latest = emailOtpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose).orElse(null);
        if (latest != null && latest.getCreatedAt() != null) {
            long seconds = ChronoUnit.SECONDS.between(latest.getCreatedAt(), LocalDateTime.now());
            if (seconds >= 0 && seconds < otpProperties.getResendCooldownSeconds()) {
                return true;
            }
        }

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long sendsLastHour = emailOtpRepository.countByEmailAndPurposeAndCreatedAtAfter(email, purpose, oneHourAgo);
        return sendsLastHour >= otpProperties.getMaxSendsPerHour();
    }

    private String hashOtp(String email, OtpPurpose purpose, String otp) {
        String pepper = otpProperties.getPepper();
        if (pepper == null || pepper.isBlank()) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "OTP pepper is not configured");
        }
        // Include email + purpose to prevent cross-purpose reuse and to bind OTP to the destination.
        String input = purpose.name() + "|" + email + "|" + otp + "|" + pepper;
        return OtpUtil.sha256Hex(input);
    }

    private String normalizeEmail(String emailRaw) {
        if (emailRaw == null) {
            return null;
        }
        String email = emailRaw.trim().toLowerCase();
        if (email.isBlank()) {
            return null;
        }
        return email;
    }

    private String buildOtpEmailHtml(String otp, int expiryMinutes) {
        // Keep content ASCII to avoid encoding surprises across clients.
        return """
                <div style="font-family:Arial, sans-serif; line-height:1.5">
                  <h2 style="margin:0 0 12px 0">WebTruyen</h2>
                  <p>Ma xac nhan (OTP) cua ban la:</p>
                  <p style="font-size:28px; font-weight:bold; letter-spacing:3px; margin:10px 0">%s</p>
                  <p>Ma co hieu luc trong %d phut. Neu ban khong yeu cau, hay bo qua email nay.</p>
                </div>
                """.formatted(otp, expiryMinutes);
    }

    private String buildResetPasswordEmailHtml(String newPassword) {
        // Keep content ASCII to avoid encoding surprises across clients.
        return """
                <div style="font-family:Arial, sans-serif; line-height:1.5">
                  <h2 style="margin:0 0 12px 0">WebTruyen</h2>
                  <p>Mat khau cua ban da duoc dat lai.</p>
                  <p>Mat khau moi cua ban la:</p>
                  <p style="font-size:22px; font-weight:bold; letter-spacing:1px; margin:10px 0">%s</p>
                  <p>Vui long dang nhap va doi mat khau ngay de bao mat tai khoan.</p>
                </div>
                """.formatted(newPassword);
    }
}
