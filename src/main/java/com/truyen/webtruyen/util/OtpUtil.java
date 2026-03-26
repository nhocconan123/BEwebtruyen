package com.truyen.webtruyen.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class OtpUtil {
    private static final SecureRandom RNG = new SecureRandom();

    private OtpUtil() {
    }

    public static String generateNumericCode(int digits) {
        if (digits < 4 || digits > 10) {
            throw new IllegalArgumentException("digits must be between 4 and 10");
        }
        int max = (int) Math.pow(10, digits);
        int value = RNG.nextInt(max);
        return String.format("%0" + digits + "d", value);
    }

    public static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return toHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // Should never happen on a standard JRE.
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }
}

