package com.quantumbanking.modules.transaction.factory;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.domain.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionFactory {

    public Transaction createDeposit(Account account, BigDecimal amount, String description) {
        return Transaction.builder()
                .accountDestiny(account)
                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .description(normalizeDescription(description))
                .build();
    }

    public Transaction createWithdrawal(Account account, BigDecimal amount, String description) {
        return Transaction.builder()
                .accountOrigin(account)
                .amount(amount)
                .type(TransactionType.WITHDRAWAL)
                .description(normalizeDescription(description))
                .build();
    }

    public Transaction createInternalTransfer(Account originAccount, Account destinyAccount, String agencyNumber, BigDecimal amount, String description) {
        return Transaction.builder()
                .accountOrigin(originAccount)
                .originName(originAccount.getClient().getName())
                .accountDestiny(destinyAccount)
                .destinyName(destinyAccount.getClient().getName())
                .destinyAgency(agencyNumber)
                .amount(amount)
                .type(TransactionType.INTERNAL_TRANSFER)
                .description(normalizeDescription(description))
                .build();
    }

    public Transaction createExternalTransfer(Account originAccount, String destinyAccount, String destinyName, String destinyAgency,
                                              String bankCode, String destinyDocument, BigDecimal amount, String description) {
        return Transaction.builder()
                .accountOrigin(originAccount)
                .originName(originAccount.getClient().getName())
                .destinyAccount(destinyAccount)
                .destinyName(destinyName)
                .destinyAgency(destinyAgency)
                .bankCode(bankCode)
                .destinyDocument(destinyDocument)
                .amount(amount)
                .type(TransactionType.EXTERNAL_TRANSFER)
                .description(normalizeDescription(description))
                .build();
    }

    public Transaction createPix(Account originAccount, BigDecimal amount, String description, String pixKey, Account destinyAccount) {
        var builder = Transaction.builder()
                .accountOrigin(originAccount)
                .originName(originAccount.getClient().getName())
                .amount(amount)
                .type(TransactionType.PIX)
                .description(normalizeDescription(description));

        if (destinyAccount != null) {
            builder.accountDestiny(destinyAccount)
                    .destinyName(destinyAccount.getClient().getName());
        } else {
            builder.destinyDocument(pixKey);
        }

        return builder.build();
    }

    private String normalizeDescription(String description) {
        return (description != null && !description.isBlank()) ? description.trim() : null;
    }
}