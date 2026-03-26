package com.truyen.webtruyen.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SendOtpRequest {
    @NotBlank(message = "email is required")
    @Email(message = "email is invalid")
    @Size(max = 150, message = "email max length is 150")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

