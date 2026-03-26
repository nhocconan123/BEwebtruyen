package com.truyen.webtruyen.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ChangePasswordConfirmRequest {
    @NotBlank(message = "currentPassword is required")
    @Size(min = 1, max = 255, message = "currentPassword max length is 255")
    private String currentPassword;

    @NotBlank(message = "otp is required")
    @Pattern(regexp = "^[0-9]{4,10}$", message = "otp must be numeric (4-10 digits)")
    private String otp;

    @NotBlank(message = "newPassword is required")
    @Size(min = 6, max = 255, message = "newPassword must be between 6 and 255 characters")
    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

