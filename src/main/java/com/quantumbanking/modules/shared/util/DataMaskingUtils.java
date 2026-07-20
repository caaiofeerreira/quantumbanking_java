package com.quantumbanking.modules.shared.util;

public final class DataMaskingUtils {

    private DataMaskingUtils(){}

    public static String maskCpf(String cpf) {

        if (cpf == null || cpf.isBlank()) return null;

        String formatted = FormattingUtils.formatCpf(cpf);

        return formatted.replaceAll("^\\d{3}\\.", "***.")
                .replaceAll("-\\d{2}$", "-**");
    }
}