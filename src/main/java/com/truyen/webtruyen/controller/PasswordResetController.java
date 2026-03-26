package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.dto.auth.MessageResponse;
import com.truyen.webtruyen.dto.auth.ResetPasswordConfirmRequest;
import com.truyen.webtruyen.dto.auth.SendOtpRequest;
import com.truyen.webtruyen.service.PasswordOtpService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/password/reset")
public class PasswordResetController {

    private final PasswordOtpService passwordOtpService;

    public PasswordResetController(PasswordOtpService passwordOtpService) {
        this.passwordOtpService = passwordOtpService;
    }

    @PostMapping
    public MessageResponse resetByEmail(@Valid @RequestBody SendOtpRequest request) {
        return passwordOtpService.resetPasswordByEmail(request.getEmail());
    }

    @PostMapping("/otp")
    public MessageResponse sendOtp(@Valid @RequestBody SendOtpRequest request) {
        return passwordOtpService.sendResetPasswordOtp(request.getEmail());
    }

    @PostMapping("/confirm")
    public MessageResponse confirm(@Valid @RequestBody ResetPasswordConfirmRequest request) {
        return passwordOtpService.confirmResetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
    }
}
