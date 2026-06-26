package com.quantumbanking.modules.shared.util;

import java.util.Set;
import java.util.regex.Pattern;

public final class FormattingUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+\\-]+@[\\w\\-]+(\\.[a-zA-Z]{2,})+$");

    private static final Set<String> DDDS_VALIDOS = Set.of(
            "11", "12", "13", "14", "15", "16", "17", "18", "19",  // SP
            "21", "22", "24",                                      // RJ
            "27", "28",                                            // ES
            "31", "32", "33", "34", "35", "37", "38",              // MG
            "41", "42", "43", "44", "45", "46",                    // PR
            "47", "48", "49",                                      // SC
            "51", "53", "54", "55",                                // RS
            "61",                                                  // DF
            "62", "64",                                            // GO
            "63",                                                  // TO
            "65", "66",                                            // MT
            "67",                                                  // MS
            "68",                                                  // AC
            "69",                                                  // RO
            "71", "73", "74", "75", "77",                          // BA
            "79",                                                  // SE
            "81", "87",                                            // PE
            "82",                                                  // AL
            "83",                                                  // PB
            "84",                                                  // RN
            "85", "88",                                            // CE
            "86", "89",                                            // PI
            "91", "93", "94",                                      // PA
            "92", "97",                                            // AM
            "95",                                                  // RR
            "96",                                                  // AP
            "98", "99"                                             // MA
    );

    public FormattingUtils() {
    }


    // ***** NORMALIZAÇÃO *****
    public static String normalizePhone(String phone) {

        if (phone == null) return null;

        String digits = phone.replaceAll("\\D", "");

        if (digits.startsWith("55") && digits.length() >= 12) {
            digits = digits.substring(2);
        }

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


    // ***** VALIDAÇÃO *****
    public static boolean isValidCpf(String cpf) {
        String digits = normalizeCpf(cpf);

        if (digits == null || digits.length() != 11) return false;
        if (digits.chars().distinct().count() == 1) return false;

        int firstDigit = calculateCpfDigit(digits.substring(0, 9), 10);
        int secondDigit = calculateCpfDigit(digits.substring(0, 9) + firstDigit, 11);

        return digits.equals(digits.substring(0, 9) + firstDigit + secondDigit);
    }

    private static int calculateCpfDigit(String base, int startWeight) {
        int sum = 0;
        int weight = startWeight;

        for (char c : base.toCharArray()) {
            sum += Character.getNumericValue(c) * weight;
            weight--;
        }

        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;

        String digits = phone.replaceAll("\\D", "");

        if (digits.startsWith("55") && digits.length() > 11) {
            digits = digits.substring(2);
        }

        if (digits.length() != 10 && digits.length() != 11) return false;

        String ddd = digits.substring(0, 2);
        if (!DDDS_VALIDOS.contains(ddd)) return false;

        char thirdDigit = digits.charAt(2);

        if (digits.length() == 11) {
            return thirdDigit == '9';
        }

        return thirdDigit >= '2' && thirdDigit <= '5';
    }

    public static boolean isValidMobilePhone(String phone) {

        if (phone == null) return false;

        String digits = phone.replaceAll("\\D", "");

        if (digits.startsWith("55") && digits.length() > 11) {
            digits = digits.substring(2);
        }

        return isValidPhone(phone) && digits.length() == 11;
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }



    // ***** FORMATAÇÃO *****
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