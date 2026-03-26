package com.truyen.webtruyen.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ResetPasswordConfirmRequest {
    @NotBlank(message = "email is required")
    @Email(message = "email is invalid")
    @Size(max = 150, message = "email max length is 150")
    private String email;

    @NotBlank(message = "otp is required")
    @Pattern(regexp = "^[0-9]{4,10}$", message = "otp must be numeric (4-10 digits)")
    private String otp;

    @NotBlank(message = "newPassword is required")
    @Size(min = 6, max = 255, message = "newPassword must be between 6 and 255 characters")
    private String newPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

