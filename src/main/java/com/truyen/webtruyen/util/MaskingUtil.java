package com.truyen.webtruyen.util;

public final class MaskingUtil {

    private MaskingUtil() {
    }

    public static String maskEmail(String emailRaw) {
        if (emailRaw == null) {
            return "";
        }
        String email = emailRaw.trim();
        if (email.isEmpty()) {
            return "";
        }
        int at = email.indexOf('@');
        if (at <= 0 || at == email.length() - 1) {
            return "***";
        }

        String local = email.substring(0, at);
        String domain = email.substring(at + 1);

        String localMasked;
        if (local.length() <= 2) {
            localMasked = local.charAt(0) + "***";
        } else {
            localMasked = local.substring(0, 2) + "***";
        }

        String domainMasked;
        int dot = domain.lastIndexOf('.');
        if (dot <= 0) {
            domainMasked = "***";
        } else {
            String dom = domain.substring(0, dot);
            String tld = domain.substring(dot);
            String domPrefix = dom.length() <= 1 ? dom : dom.substring(0, 1);
            domainMasked = domPrefix + "***" + tld;
        }

        return localMasked + "@" + domainMasked;
    }
}

