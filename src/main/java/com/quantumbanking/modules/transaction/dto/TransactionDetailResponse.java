package com.quantumbanking.modules.transaction.dto;

public sealed interface TransactionDetailResponse
        permits DepositResponseDTO, ExternalTransactionResponseDTO, InternalTransactionResponseDTO, LoanTransactionDetailDTO, PixTransactionResponseDTO, WithdrawResponseDTO {
}
