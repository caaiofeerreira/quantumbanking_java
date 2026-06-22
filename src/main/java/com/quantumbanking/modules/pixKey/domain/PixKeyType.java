package com.quantumbanking.modules.pixKey.domain;

import com.quantumbanking.infra.exception.InvalidPixKeyTypeException;

import java.util.Arrays;
import java.util.regex.Pattern;

public enum PixKeyType {

    CPF("^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$"),
    CNPJ("^\\d{2}\\.?\\d{3}\\.?\\d{3}\\/?\\d{4}-?\\d{2}$"),
    EMAIL("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"),
    PHONE("^\\+\\d{10,13}$"),
    RANDOM("^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");

    private final Pattern pattern;

    PixKeyType(String regex) {
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    public boolean matches(String key) {
        return pattern.matcher(key).matches();
    }

    public static PixKeyType detect(String key) {

        if (key == null || key.isBlank()) {
            throw new InvalidPixKeyTypeException("Chave Pix não pode ser nula ou vazia.");
        }
        return Arrays.stream(values())
                .filter(type -> type.matches(key))
                .findFirst()
                .orElseThrow(() -> new InvalidPixKeyTypeException("Formato de chave Pix inválido: " + key));
    }
}