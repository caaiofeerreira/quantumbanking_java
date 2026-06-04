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
}