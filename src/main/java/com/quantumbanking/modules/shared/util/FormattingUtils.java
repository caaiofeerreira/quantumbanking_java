package com.quantumbanking.modules.shared.util;

public final class FormattingUtils {

    private FormattingUtils() {}

    public static String formatAccountNumber(String number) {

        if (number == null || number.length() < 2) {
            return number;
        }
        int splitIndex = number.length() - 1;
        return number.substring(0, splitIndex) + "-" + number.substring(splitIndex);
    }

    public static String formatPhone(String phone) {
        if (phone == null) return null;

        String digits = phone.replaceAll("\\D", "");

        if (digits.startsWith("55")) {
            digits = digits.substring(2);
        }

        return "(%s) %s-%s".formatted(
                digits.substring(0, 2),
                digits.substring(2, 7),
                digits.substring(7)
        );
    }

    public static String formatCpf(String cpf) {
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}