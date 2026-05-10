package com.quantumbanking.modules.account.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.quantumbanking.infra.exception.InvalidPixKeyTypeException;

public enum PixKeyType {
    CPF,
    CNPJ,
    EMAIL,
    PHONE,
    RANDOM;

    @JsonCreator
    public static PixKeyType fromValue(String value) {
        if (value == null) return null;
        try {
            return PixKeyType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidPixKeyTypeException("Tipo de chave Pix invalido: " + value);
        }
    }
}