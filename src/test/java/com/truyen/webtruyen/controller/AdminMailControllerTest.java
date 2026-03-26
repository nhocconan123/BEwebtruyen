package com.truyen.webtruyen.controller;

import com.truyen.webtruyen.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.truyen.webtruyen.security.jwt.JwtService;
import com.truyen.webtruyen.service.CustomUserDetailsService;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminMailController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminMailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService;

    // Needed because JwtAuthenticationFilter is a @Component in this project.
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void sendTest_callsEmailService_andReturnsOk() throws Exception {
        mockMvc.perform(post("/api/admin/mail/test")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "to": "USER@Example.com",
                                  "subject": "Hi",
                                  "html": "<b>Test</b>"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email sent"));

        verify(emailService).sendHtml(eq("user@example.com"), eq("Hi"), eq("<b>Test</b>"));
    }
}
