package com.quantumbanking.modules.account.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.quantumbanking.infra.exception.InvalidAccountTypeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum AccountType {
    POUPANCA(4, new BigDecimal("6.50")),
    CORRENTE(10, new BigDecimal("4.50")),
    JURIDICA(20, new BigDecimal("8.00"));

    private final int freeWithdrawals;
    private final BigDecimal feeAmount;

    @JsonCreator
    public static AccountType fromValue(String value) {

        if (value == null) return null;

        String normalized = normalize(value);

        try {
            return AccountType.valueOf(normalized);

        } catch (IllegalArgumentException e) {
            String validValue = Arrays
                    .stream(AccountType.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new InvalidAccountTypeException("Tipo de conta invalido: '" + value + "'. Valores aceitos: " + validValue);
        }
    }

    private static String normalize(String value) {

        String decomposed = Normalizer.normalize(value.trim(), Normalizer.Form.NFD);
        return Pattern.compile("\\p{M}").matcher(decomposed).replaceAll("").toUpperCase();
    }
}