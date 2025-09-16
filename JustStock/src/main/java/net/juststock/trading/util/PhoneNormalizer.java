package net.juststock.trading.util;

public final class PhoneNormalizer {
    private PhoneNormalizer() {}

    public static String normalizeE164Loose(String input) {
        if (input == null) return null;
        String s = input.trim();
        if (s.startsWith("00")) {
            s = "+" + s.substring(2);
        }
        s = s.replaceAll("[^+\\d]", "");
        if (!s.startsWith("+") && s.matches("^[1-9]\\d{7,14}$")) {
            s = "+" + s;
        }
        return s;
    }
}

