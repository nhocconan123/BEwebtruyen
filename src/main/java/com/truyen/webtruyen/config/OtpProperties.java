package com.truyen.webtruyen.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.otp")
public class OtpProperties {
    private String pepper;
    private int digits = 6;
    private int expiryMinutes = 10;
    private int resendCooldownSeconds = 60;
    private int maxSendsPerHour = 5;
    private int maxVerifyAttempts = 5;

    public String getPepper() {
        return pepper;
    }

    public void setPepper(String pepper) {
        this.pepper = pepper;
    }

    public int getDigits() {
        return digits;
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }

    public int getExpiryMinutes() {
        return expiryMinutes;
    }

    public void setExpiryMinutes(int expiryMinutes) {
        this.expiryMinutes = expiryMinutes;
    }

    public int getResendCooldownSeconds() {
        return resendCooldownSeconds;
    }

    public void setResendCooldownSeconds(int resendCooldownSeconds) {
        this.resendCooldownSeconds = resendCooldownSeconds;
    }

    public int getMaxSendsPerHour() {
        return maxSendsPerHour;
    }

    public void setMaxSendsPerHour(int maxSendsPerHour) {
        this.maxSendsPerHour = maxSendsPerHour;
    }

    public int getMaxVerifyAttempts() {
        return maxVerifyAttempts;
    }

    public void setMaxVerifyAttempts(int maxVerifyAttempts) {
        this.maxVerifyAttempts = maxVerifyAttempts;
    }
}

