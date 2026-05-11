package com.quantumbanking.modules.loan.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class LoanCalculator {

    public BigDecimal calculateInstallmentAmount(BigDecimal amount, BigDecimal interestRate, Integer installments) {

        if (interestRate.compareTo(BigDecimal.ZERO) == 0) {
            return amount.divide(BigDecimal.valueOf(installments), 2, RoundingMode.HALF_UP);
        }

        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal factor = monthlyRate.add(BigDecimal.ONE).pow(installments);

        BigDecimal numerator = monthlyRate.multiply(factor);
        BigDecimal denominator = factor.subtract(BigDecimal.ONE);

        return amount.multiply(numerator)
                .divide(denominator, 2, RoundingMode.HALF_UP);
    }


    public BigDecimal calculateTotal(BigDecimal installmentAmount, Integer installments) {
        return installmentAmount.multiply(BigDecimal.valueOf(installments))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
