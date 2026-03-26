package com.truyen.webtruyen.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SendTestEmailRequest {

    @NotBlank(message = "to is required")
    @Email(message = "to must be a valid email")
    private String to;

    private String subject;

    private String html;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}

