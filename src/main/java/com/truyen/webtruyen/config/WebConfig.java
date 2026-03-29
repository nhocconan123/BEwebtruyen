package com.truyen.webtruyen.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final CorsProperties corsProperties;

    public WebConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedOrigins = sanitize(corsProperties.getAllowedOrigins());
        if (allowedOrigins.length == 0) {
            return;
        }

        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods(sanitize(corsProperties.getAllowedMethods()))
                .allowedHeaders(sanitize(corsProperties.getAllowedHeaders()))
                .exposedHeaders(sanitize(corsProperties.getExposedHeaders()))
                .allowCredentials(corsProperties.isAllowCredentials())
                .maxAge(corsProperties.getMaxAgeSeconds());
    }

    private String[] sanitize(List<String> values) {
        return values == null
                ? new String[0]
                : values.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .toArray(String[]::new);
    }
}
