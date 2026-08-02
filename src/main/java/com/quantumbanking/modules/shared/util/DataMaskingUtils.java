package com.quantumbanking.modules.shared.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DataMaskingUtils {

    private DataMaskingUtils(){}

    public static String maskCpf(String cpf) {

        if (cpf == null || cpf.isBlank()) return null;

        String formatted = FormattingUtils.formatCpf(cpf);

        return formatted.replaceAll("^\\d{3}\\.", "***.")
                .replaceAll("-\\d{2}$", "-**");
    }

    public static String maskCnpj(String cnpj) {

        if (cnpj == null || cnpj.isBlank()) return null;

        String formatted = FormattingUtils.formatCnpj(cnpj);

        return formatted.replaceAll("/\\d{4}-\\d{2}$", "/****-**");
    }

    public static String maskDocument(String document) {

        if (document == null || document.isBlank()) return null;

        String cleanDocument = document.replaceAll("\\D", "");

        if (cleanDocument.length() <= 11) {
            return maskCpf(document);
        } else {
            return maskCnpj(document);
        }
    }

    public static String maskPhone(String phone) {

        if (phone == null || phone.isBlank()) return null;

        String formatted = FormattingUtils.formatPhone(phone);
        Matcher matcher = Pattern.compile("\\d{4,5}-").matcher(formatted);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String masked = "*".repeat(matcher.group().length() - 1) + "-";
            matcher.appendReplacement(sb, masked);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String maskEmail(String email) {

        if (email == null || email.isBlank()) return null;

        int atIndex = email.indexOf("@");
        if (atIndex <= 0) return email;

        String name = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        String maskedName = name.length() <= 2
                ? name.charAt(0) + "*"
                : name.charAt(0) + "*****";

        return maskedName + domain;
    }
}