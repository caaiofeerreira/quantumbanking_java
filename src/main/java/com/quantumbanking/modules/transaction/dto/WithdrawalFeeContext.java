package com.quantumbanking.modules.transaction.dto;

public record WithdrawalFeeContext(long withdrawalsThisMonth, int freeWithdrawals) {
}