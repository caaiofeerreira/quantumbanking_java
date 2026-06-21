package com.quantumbanking.modules.client.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.quantumbanking.infra.exception.InvalidClientTypeException;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum ClientType {

    FISICA,
    JURIDICA;

    @JsonCreator
    public static ClientType fromValue(String value) {

        if (value == null) return null;

        String normalized = normalize(value);

        try {
            return ClientType.valueOf(normalized);

        } catch (IllegalArgumentException e) {

            String validValue = Arrays
                    .stream(ClientType.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new InvalidClientTypeException(
                    "Tipo de cliente invalido: '" + value + "'. Valores aceitos: " + validValue);
        }
    }

    private static String normalize(String value) {

        String decomposed = Normalizer.normalize(value.trim(), Normalizer.Form.NFD);
        return Pattern.compile("\\p{M}").matcher(decomposed).replaceAll("").toUpperCase();
    }
}