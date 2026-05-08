package com.quantumbanking.modules.transaction.dto;

public record AccountInfoDTO(String name,
                             String document,
                             String bank,
                             String agency,
                             String accountNumber) {
}
