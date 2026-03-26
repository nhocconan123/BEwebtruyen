package com.truyen.webtruyen.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {
    @NotBlank(message = "currentPassword is required")
    @Size(min = 1, max = 255, message = "currentPassword max length is 255")
    private String currentPassword;

    @NotBlank(message = "newPassword is required")
    @Size(min = 6, max = 255, message = "newPassword must be between 6 and 255 characters")
    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

