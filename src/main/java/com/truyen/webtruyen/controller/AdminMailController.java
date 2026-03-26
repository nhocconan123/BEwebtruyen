package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.dto.admin.SendTestEmailRequest;
import com.truyen.webtruyen.dto.auth.MessageResponse;
import com.truyen.webtruyen.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/admin/mail")
public class AdminMailController {

    private final EmailService emailService;

    public AdminMailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/test")
    public MessageResponse sendTest(@Valid @RequestBody SendTestEmailRequest request) {
        String to = request.getTo().trim().toLowerCase();
        String subject = request.getSubject() == null || request.getSubject().isBlank()
                ? "WebTruyen SMTP Test"
                : request.getSubject().trim();
        String html = request.getHtml() == null || request.getHtml().isBlank()
                ? defaultHtml()
                : request.getHtml();

        emailService.sendHtml(to, subject, html);
        return new MessageResponse("Email sent");
    }

    private String defaultHtml() {
        return """
                <div style="font-family:Arial, sans-serif; line-height:1.5">
                  <h2 style="margin:0 0 12px 0">WebTruyen</h2>
                  <p>This is a test email sent at: <b>%s</b></p>
                </div>
                """.formatted(OffsetDateTime.now());
    }
}

