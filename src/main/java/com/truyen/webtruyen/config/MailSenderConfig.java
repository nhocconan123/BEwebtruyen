package com.truyen.webtruyen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Configuration
public class MailSenderConfig {

    @Bean
    public JavaMailSender javaMailSender(SpringMailProperties properties) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(properties.getHost());
        if (properties.getPort() != null) sender.setPort(properties.getPort());
        sender.setUsername(properties.getUsername());
        sender.setPassword(properties.getPassword());
        sender.setProtocol(properties.getProtocol());
        sender.setDefaultEncoding(StandardCharsets.UTF_8.name());

        Properties javaMailProps = sender.getJavaMailProperties();
        if (properties.getProperties() != null) {
            for (var e : properties.getProperties().entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    javaMailProps.put(e.getKey(), e.getValue());
                }
            }
        }
        return sender;
    }
}

