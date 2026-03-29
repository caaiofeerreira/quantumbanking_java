package com.quantumbanking.modules.transaction.dto;

import java.math.BigDecimal;

public record InternalTransactionRequestDTO(String accountNumber,
                                            BigDecimal amount,
                                            String agencyNumber) {
}