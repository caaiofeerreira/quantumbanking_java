package com.quantumbanking.modules.transaction.formater;

import com.quantumbanking.modules.transaction.domain.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionStatementFormatter {

    public String getDisplayDescription(Transaction transaction, boolean isOrigin) {
        if (transaction.getDescription() != null && !transaction.getDescription().isBlank()) {
            return transaction.getDescription();
        }
        return transaction.getType().getDisplayName(isOrigin);
    }

    public String getCounterpartName(Transaction transaction, boolean isOrigin) {
        return transaction.getType().getCounterpartName(transaction, isOrigin);
    }
}