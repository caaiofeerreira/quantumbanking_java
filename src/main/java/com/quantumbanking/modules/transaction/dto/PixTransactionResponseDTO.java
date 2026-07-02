package com.quantumbanking.modules.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quantumbanking.modules.pixKey.domain.PixKeyType;
import com.quantumbanking.modules.transaction.domain.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PixTransactionResponseDTO(UUID id,
                                        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
                                        LocalDateTime createdAt,
                                        TransactionType type,
                                        BigDecimal amount,
                                        String pixKey,
                                        PixKeyType pixKeyType,
                                        String description,
                                        PixAccountInfoDTO destinationAccount,
                                        PixAccountInfoDTO originAccount) implements TransactionDetailResponse {

}