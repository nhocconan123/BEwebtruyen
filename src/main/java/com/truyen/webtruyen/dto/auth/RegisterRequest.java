package com.truyen.webtruyen.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "username is required")
    @Size(min = 3, max = 100, message = "username must be between 3 and 100 characters")
    private String username;

    @NotBlank(message = "email is required")
    @Email(message = "email is invalid")
    @Size(max = 150, message = "email max length is 150")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 255, message = "password must be between 6 and 255 characters")
    private String password;

    @Size(max = 255, message = "avatar max length is 255")
    private String avatar;

    @Size(max = 1000, message = "bio max length is 1000")
    private String bio;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
