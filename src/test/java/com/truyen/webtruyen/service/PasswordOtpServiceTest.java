package com.truyen.webtruyen.service;

import com.truyen.webtruyen.config.OtpProperties;
import com.truyen.webtruyen.dto.auth.MessageResponse;
import com.truyen.webtruyen.entity.EmailOtp;
import com.truyen.webtruyen.entity.User;
import com.truyen.webtruyen.entity.enums.OtpPurpose;
import com.truyen.webtruyen.repository.EmailOtpRepository;
import com.truyen.webtruyen.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PasswordOtpServiceTest {

    @Test
    void resetPasswordByEmail_success_resetsToDefaultAndSendsEmail_whenUserExistsAndNotThrottled() {
        UserRepository userRepository = mock(UserRepository.class);
        EmailOtpRepository emailOtpRepository = mock(EmailOtpRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        EmailService emailService = mock(EmailService.class);

        OtpProperties otpProperties = new OtpProperties();
        otpProperties.setPepper("unit-test-pepper");
        otpProperties.setDigits(6);
        otpProperties.setExpiryMinutes(10);
        otpProperties.setResendCooldownSeconds(60);
        otpProperties.setMaxSendsPerHour(5);
        otpProperties.setMaxVerifyAttempts(5);

        when(emailOtpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc(anyString(), any()))
                .thenReturn(Optional.empty());
        when(emailOtpRepository.countByEmailAndPurposeAndCreatedAtAfter(anyString(), any(), any(LocalDateTime.class)))
                .thenReturn(0L);

        User user = new User();
        user.setId(88L);
        user.setEmail("huy187084@gmail.com");
        user.setPassword("old-hash");

        when(userRepository.findByEmail("huy187084@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("123456")).thenReturn("hashed-default");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(emailOtpRepository.save(any(EmailOtp.class))).thenAnswer(inv -> inv.getArgument(0));

        PasswordOtpService service = new PasswordOtpService(
                userRepository,
                emailOtpRepository,
                passwordEncoder,
                emailService,
                otpProperties
        );

        MessageResponse resp = service.resetPasswordByEmail("  HUY187084@gmail.com  ");
        assertEquals("If the email exists, a new password has been sent.", resp.getMessage());
        assertEquals("hashed-default", user.getPassword());

        verify(emailService).assertConfigured();
        verify(userRepository).save(user);

        ArgumentCaptor<EmailOtp> captor = ArgumentCaptor.forClass(EmailOtp.class);
        verify(emailOtpRepository).save(captor.capture());
        EmailOtp row = captor.getValue();

        assertEquals(88L, row.getUserId());
        assertEquals("huy187084@gmail.com", row.getEmail());
        assertEquals(OtpPurpose.RESET_PASSWORD, row.getPurpose());
        assertNotNull(row.getExpiresAt());
        assertNotNull(row.getConsumedAt());
        assertTrue(row.getOtpHash().matches("^[0-9a-f]{64}$"));

        verify(emailService).sendHtml(
                eq("huy187084@gmail.com"),
                contains("Password reset"),
                contains("Mat khau moi")
        );
    }

    @Test
    void resetPasswordByEmail_returnsGenericMessage_whenEmailDoesNotExist() {
        UserRepository userRepository = mock(UserRepository.class);
        EmailOtpRepository emailOtpRepository = mock(EmailOtpRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        EmailService emailService = mock(EmailService.class);

        OtpProperties otpProperties = new OtpProperties();
        otpProperties.setPepper("unit-test-pepper");
        otpProperties.setDigits(6);
        otpProperties.setExpiryMinutes(10);
        otpProperties.setResendCooldownSeconds(60);
        otpProperties.setMaxSendsPerHour(5);
        otpProperties.setMaxVerifyAttempts(5);

        when(emailOtpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc(anyString(), any()))
                .thenReturn(Optional.empty());
        when(emailOtpRepository.countByEmailAndPurposeAndCreatedAtAfter(anyString(), any(), any(LocalDateTime.class)))
                .thenReturn(0L);
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        PasswordOtpService service = new PasswordOtpService(
                userRepository,
                emailOtpRepository,
                passwordEncoder,
                emailService,
                otpProperties
        );

        MessageResponse resp = service.resetPasswordByEmail("nobody@example.com");
        assertEquals("If the email exists, a new password has been sent.", resp.getMessage());

        verify(emailService).assertConfigured();
        verify(userRepository, never()).save(any(User.class));
        verify(emailOtpRepository, never()).save(any(EmailOtp.class));
        verify(emailService, never()).sendHtml(anyString(), anyString(), anyString());
    }

    @Test
    void sendResetPasswordOtp_success_persistsOtpAndSendsEmail_whenUserExistsAndNotThrottled() {
        UserRepository userRepository = mock(UserRepository.class);
        EmailOtpRepository emailOtpRepository = mock(EmailOtpRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        EmailService emailService = mock(EmailService.class);

        OtpProperties otpProperties = new OtpProperties();
        otpProperties.setPepper("unit-test-pepper");
        otpProperties.setDigits(6);
        otpProperties.setExpiryMinutes(10);
        otpProperties.setResendCooldownSeconds(60);
        otpProperties.setMaxSendsPerHour(5);
        otpProperties.setMaxVerifyAttempts(5);

        // Not throttled.
        when(emailOtpRepository.findTopByEmailAndPurposeOrderByCreatedAtDesc(anyString(), any()))
                .thenReturn(Optional.empty());
        when(emailOtpRepository.countByEmailAndPurposeAndCreatedAtAfter(anyString(), any(), any(LocalDateTime.class)))
                .thenReturn(0L);

        User user = new User();
        user.setId(123L);
        user.setEmail("huy187084@gmail.com");
        when(userRepository.findByEmail("huy187084@gmail.com")).thenReturn(Optional.of(user));

        when(emailOtpRepository.save(any(EmailOtp.class))).thenAnswer(inv -> inv.getArgument(0));

        PasswordOtpService service = new PasswordOtpService(
                userRepository,
                emailOtpRepository,
                passwordEncoder,
                emailService,
                otpProperties
        );

        MessageResponse resp = service.sendResetPasswordOtp("  HUY187084@gmail.com  ");
        assertEquals("If the email exists, an OTP has been sent.", resp.getMessage());

        verify(emailService).assertConfigured();

        ArgumentCaptor<EmailOtp> captor = ArgumentCaptor.forClass(EmailOtp.class);
        verify(emailOtpRepository).save(captor.capture());
        EmailOtp row = captor.getValue();

        assertEquals(123L, row.getUserId());
        assertEquals("huy187084@gmail.com", row.getEmail());
        assertEquals(OtpPurpose.RESET_PASSWORD, row.getPurpose());
        assertNotNull(row.getExpiresAt());
        assertNotNull(row.getOtpHash());
        assertTrue(row.getOtpHash().matches("^[0-9a-f]{64}$"));

        verify(emailService).sendHtml(
                eq("huy187084@gmail.com"),
                contains("Reset password"),
                contains("Ma xac nhan")
        );
    }

    @Test
    void changePassword_success_updatesPassword_whenCurrentPasswordMatches_andNewPasswordDiffers() {
        UserRepository userRepository = mock(UserRepository.class);
        EmailOtpRepository emailOtpRepository = mock(EmailOtpRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        EmailService emailService = mock(EmailService.class);

        OtpProperties otpProperties = new OtpProperties();
        otpProperties.setPepper("unit-test-pepper");

        User user = new User();
        user.setId(1L);
        user.setPassword("hashed-old");

        when(passwordEncoder.matches("oldPass", "hashed-old")).thenReturn(true);
        when(passwordEncoder.matches("newPass", "hashed-old")).thenReturn(false);
        when(passwordEncoder.encode("newPass")).thenReturn("hashed-new");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        PasswordOtpService service = new PasswordOtpService(
                userRepository,
                emailOtpRepository,
                passwordEncoder,
                emailService,
                otpProperties
        );

        MessageResponse resp = service.changePassword(user, "oldPass", "newPass");
        assertEquals("Password updated", resp.getMessage());
        assertEquals("hashed-new", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_rejects_whenNewPasswordSameAsCurrent() {
        UserRepository userRepository = mock(UserRepository.class);
        EmailOtpRepository emailOtpRepository = mock(EmailOtpRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        EmailService emailService = mock(EmailService.class);

        OtpProperties otpProperties = new OtpProperties();
        otpProperties.setPepper("unit-test-pepper");

        User user = new User();
        user.setId(1L);
        user.setPassword("hashed-old");

        // This matcher is evaluated for both `currentPassword` and `newPassword`.
        when(passwordEncoder.matches("oldPass", "hashed-old")).thenReturn(true);

        PasswordOtpService service = new PasswordOtpService(
                userRepository,
                emailOtpRepository,
                passwordEncoder,
                emailService,
                otpProperties
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.changePassword(user, "oldPass", "oldPass"));
        assertEquals(400, ex.getStatusCode().value());
        assertEquals("New password must be different", ex.getReason());
    }

    @Test
    void changePassword_rejects_whenCurrentPasswordInvalid() {
        UserRepository userRepository = mock(UserRepository.class);
        EmailOtpRepository emailOtpRepository = mock(EmailOtpRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        EmailService emailService = mock(EmailService.class);

        OtpProperties otpProperties = new OtpProperties();
        otpProperties.setPepper("unit-test-pepper");

        User user = new User();
        user.setId(1L);
        user.setPassword("hashed-old");

        when(passwordEncoder.matches("badPass", "hashed-old")).thenReturn(false);

        PasswordOtpService service = new PasswordOtpService(
                userRepository,
                emailOtpRepository,
                passwordEncoder,
                emailService,
                otpProperties
        );

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.changePassword(user, "badPass", "newPass"));
        assertEquals(401, ex.getStatusCode().value());
        assertEquals("Invalid credentials", ex.getReason());
    }
}
