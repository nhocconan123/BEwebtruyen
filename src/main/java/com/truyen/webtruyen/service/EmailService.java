package com.truyen.webtruyen.service;

import com.truyen.webtruyen.config.SpringMailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringMailProperties mailProperties;
    private final String from;

    public EmailService(JavaMailSender mailSender,
                        SpringMailProperties mailProperties,
                        @Value("${spring.mail.username:}") String from) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
        this.from = from;
    }

    public void sendHtml(String to, String subject, String html) {
        assertConfigured();
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            if (from != null && !from.isBlank()) {
                helper.setFrom(from);
            }
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MailException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Failed to send email. Check SMTP config.");
        } catch (MessagingException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Failed to build email.");
        }
    }

    public void assertConfigured() {
        // For Gmail SMTP, username + password are required.
        boolean ok = mailProperties != null
                && mailProperties.getHost() != null
                && !mailProperties.getHost().isBlank()
                && mailProperties.getUsername() != null
                && !mailProperties.getUsername().isBlank()
                && mailProperties.getPassword() != null
                && !mailProperties.getPassword().isBlank();
        if (!ok) {
            // This is a server-side configuration issue; 503 is clearer than 500 for clients.
            throw new ResponseStatusException(
                    SERVICE_UNAVAILABLE,
                    "Email SMTP is not configured (set MAIL_USERNAME/MAIL_PASSWORD or spring.mail.*)"
            );
        }
    }
}
