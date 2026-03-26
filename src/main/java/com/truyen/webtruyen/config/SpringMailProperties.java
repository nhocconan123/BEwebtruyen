package com.truyen.webtruyen.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Spring Boot 4.x in this project does not provide mail auto-configuration.
 * Bind the standard spring.mail.* properties ourselves.
 */
@ConfigurationProperties(prefix = "spring.mail")
public class SpringMailProperties {
    private String host = "localhost";
    private Integer port = 25;
    private String username;
    private String password;
    private String protocol = "smtp";

    /**
     * Maps to spring.mail.properties.*
     */
    private Map<String, String> properties = new LinkedHashMap<>();

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}

