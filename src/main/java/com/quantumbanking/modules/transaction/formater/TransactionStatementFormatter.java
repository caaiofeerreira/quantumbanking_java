package com.quantumbanking.modules.transaction.formater;


import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.resolver.AccountHolderInfoResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionStatementFormatter {

    private final AccountHolderInfoResolver accountHolderInfoResolver;

    public String getDisplayDescription(Transaction transaction, boolean isOrigin) {

        if (transaction.getDescription() != null && !transaction.getDescription().isBlank()) {
            return transaction.getDescription();
        }

        return transaction.getType().getDisplayName(isOrigin);
    }

    public String getCounterpartName(Transaction transaction, boolean isOrigin) {
        return transaction.getType().getFixedCounterpartName(isOrigin)
                .orElseGet(() -> resolveAccountBasedCounterpart(transaction, isOrigin));
    }

    private String resolveAccountBasedCounterpart(Transaction transaction, boolean isOrigin) {
        if (isOrigin) {
            if (transaction.getDestinationAccount() != null) {
                return accountHolderInfoResolver.resolve(transaction.getDestinationAccount()).name();
            }
            if (transaction.getDestinationName() != null) return transaction.getDestinationName();
            if (transaction.getDestinationBankName() != null) return transaction.getDestinationBankName();
            return "Destinatário não identificado";
        } else {
            if (transaction.getOriginAccount() != null) {
                return accountHolderInfoResolver.resolve(transaction.getOriginAccount()).name();
            }
            if (transaction.getOriginName() != null) return transaction.getOriginName();
            return "Origem Externa";
        }
    }
}