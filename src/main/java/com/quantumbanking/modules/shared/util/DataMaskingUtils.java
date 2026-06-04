package com.quantumbanking.modules.shared.util;

public final class DataMaskingUtils {

    private DataMaskingUtils(){}

    public static String maskCpf(String cpf) {

        return cpf.replaceAll("^\\d{3}\\.", "***.")
                .replaceAll("-\\d{2}$", "-**");
    }
}