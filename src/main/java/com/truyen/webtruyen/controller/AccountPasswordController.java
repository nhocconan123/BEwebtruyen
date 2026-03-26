package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.dto.auth.ChangePasswordConfirmRequest;
import com.truyen.webtruyen.dto.auth.ChangePasswordRequest;
import com.truyen.webtruyen.dto.auth.MessageResponse;
import com.truyen.webtruyen.entity.User;
import com.truyen.webtruyen.service.CurrentUserService;
import com.truyen.webtruyen.service.PasswordOtpService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account/password")
public class AccountPasswordController {

    private final CurrentUserService currentUserService;
    private final PasswordOtpService passwordOtpService;

    public AccountPasswordController(CurrentUserService currentUserService, PasswordOtpService passwordOtpService) {
        this.currentUserService = currentUserService;
        this.passwordOtpService = passwordOtpService;
    }

    @PostMapping("/change/otp")
    public MessageResponse sendChangeOtp() {
        User currentUser = currentUserService.getCurrentUser();
        return passwordOtpService.sendChangePasswordOtp(currentUser);
    }

    @PostMapping("/change/confirm")
    public MessageResponse confirmChange(@Valid @RequestBody ChangePasswordConfirmRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        return passwordOtpService.confirmChangePassword(currentUser, request.getCurrentPassword(), request.getOtp(), request.getNewPassword());
    }

    // Simple change password flow: requires authentication + current password. No email OTP.
    @PostMapping("/change")
    public MessageResponse change(@Valid @RequestBody ChangePasswordRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        return passwordOtpService.changePassword(currentUser, request.getCurrentPassword(), request.getNewPassword());
    }
}
