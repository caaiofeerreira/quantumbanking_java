package com.quantumbanking.modules.transaction.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TransactionClassifier {

    @Value("${bank.compe}")
    private String compe;

    public boolean isExternal(TransactionType type, String destinationBankCompe) {

        return switch(type) {
            case DEPOSIT, WITHDRAWAL, FEE, LOAN, INTERNAL_TRANSFER -> false;
            case EXTERNAL_TRANSFER -> true;
            case PIX -> isDifferentBank(destinationBankCompe);
        };
    }

    private boolean isDifferentBank(String destinationBankCompe) {
        return destinationBankCompe != null && !compe.equals(destinationBankCompe);
    }
}