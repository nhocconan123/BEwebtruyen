package com.truyen.webtruyen.service;

import com.truyen.webtruyen.config.SpringMailProperties;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

class EmailServiceTest {

    @Test
    void assertConfigured_throws503_whenMissingUsernameOrPassword() {
        SpringMailProperties props = new SpringMailProperties();
        props.setHost("smtp.gmail.com");
        props.setPort(587);
        props.setUsername("");
        props.setPassword("");

        EmailService svc = new EmailService(noopMailSender(), props, "");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, svc::assertConfigured);
        assertEquals(SERVICE_UNAVAILABLE, ex.getStatusCode());
    }

    @Test
    void assertConfigured_ok_whenHostUsernamePasswordPresent() {
        SpringMailProperties props = new SpringMailProperties();
        props.setHost("smtp.gmail.com");
        props.setPort(587);
        props.setUsername("x@example.com");
        props.setPassword("app-password");

        EmailService svc = new EmailService(noopMailSender(), props, "x@example.com");

        assertDoesNotThrow(svc::assertConfigured);
    }

    private static JavaMailSender noopMailSender() {
        // We only test configuration checks here; sending is exercised elsewhere.
        return new org.springframework.mail.javamail.JavaMailSenderImpl();
    }
}

