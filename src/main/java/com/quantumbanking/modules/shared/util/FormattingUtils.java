package com.quantumbanking.modules.shared.util;

public final class FormattingUtils {

    private FormattingUtils() {
    }

    // ***** NORMALIZAÇÃO ***** \\

    public static String normalizePhone(String phone) {

        if (phone == null) return null;

        String digits = phone.replaceAll("\\D", "");
        return "+55" + digits;
    }

    public static String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase();
    }

    public static String normalizeCpf(String cpf) {
        if (cpf == null) return null;
        return cpf.replaceAll("[^0-9]", "");
    }


    // ***** FORMATAÇÃO *****\\

    public static String formatAccountNumber(String number) {

        if (number == null) return null;

        String cleaned = number.trim();
        if (cleaned.length() < 2) {
            return cleaned;
        }

        int splitIndex = cleaned.length() - 1;
        return cleaned.substring(0, splitIndex) + "-" + cleaned.substring(splitIndex);
    }

    public static String formatPhone(String phone) {

        if (phone == null) return null;

        String digits = phone.replaceAll("\\D", "");

        if (digits.startsWith("55")) {
            digits = digits.substring(2);
        }

        if (digits.length() != 10 && digits.length() != 11) {
            return digits;
        }

        if (digits.length() == 11) {
            return "(%s) %s-%s".formatted(
                    digits.substring(0, 2),
                    digits.substring(2, 7),
                    digits.substring(7)
            );
        }

        return "(%s) %s-%s".formatted(
                digits.substring(0, 2),
                digits.substring(2, 6),
                digits.substring(6)
        );
    }

    public static String formatCpf(String cpf) {

        if (cpf == null) return null;

        String digits = normalizeCpf(cpf);
        return digits.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}