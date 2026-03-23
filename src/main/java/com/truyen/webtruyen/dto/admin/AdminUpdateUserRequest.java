package com.truyen.webtruyen.dto.admin;

import com.truyen.webtruyen.entity.enums.UserRole;
import jakarta.validation.constraints.Size;

/**
 * Partial update payload for admin user edits.
 * All fields are optional; null means "do not change".
 */
public class AdminUpdateUserRequest {

    @Size(max = 100, message = "username max length is 100")
    private String username;

    private UserRole role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}

