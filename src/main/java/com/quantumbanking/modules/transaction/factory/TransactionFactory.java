package com.quantumbanking.modules.transaction.factory;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.domain.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionFactory {

    public Transaction createDeposit(Account account, BigDecimal amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setAccountDestiny(account);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setDescription(normalizeDescription(description));
        return transaction;
    }

    public Transaction createWithdrawal(Account account, BigDecimal amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setAccountOrigin(account);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setDescription(normalizeDescription(description));
        return transaction;
    }

    public Transaction createInternalTransfer(Account accountOrigin, Account accountDestiny, String agencyNumber, BigDecimal amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setAccountOrigin(accountOrigin);
        transaction.setOriginName(accountOrigin.getClient().getName());
        transaction.setAccountDestiny(accountDestiny);
        transaction.setDestinyName(accountDestiny.getClient().getName());
        transaction.setDestinyAgency(agencyNumber);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.INTERNAL_TRANSFER);
        transaction.setDescription(normalizeDescription(description));
        return transaction;
    }

    public Transaction createExternalTransfer(Account accountOrigin, String destinyName, String destinyAccount, String destinyAgency,
                                              String bankCode, String destinyDocument, BigDecimal amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setAccountOrigin(accountOrigin);
        transaction.setOriginName(accountOrigin.getClient().getName());
        transaction.setDestinyAccount(destinyAccount);
        transaction.setDestinyName(destinyName);
        transaction.setDestinyAgency(destinyAgency);
        transaction.setBankCode(bankCode);
        transaction.setDestinyDocument(destinyDocument);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.EXTERNAL_TRANSFER);
        transaction.setDescription(normalizeDescription(description));
        return transaction;
    }

    public Transaction createPix(Account origin, BigDecimal amount, String description, String pixKey, Account destinyAccount) {
        Transaction transaction = new Transaction();
        transaction.setAccountOrigin(origin);
        transaction.setOriginName(origin.getClient().getName());
        transaction.setAmount(amount);
        transaction.setType(TransactionType.PIX);
        transaction.setDescription(normalizeDescription(description));

        if (destinyAccount != null) {
            transaction.setAccountDestiny(destinyAccount);
            transaction.setDestinyName(destinyAccount.getClient().getName());
        } else {
            transaction.setDestinyDocument(pixKey);
        }

        return transaction;
    }

    private String normalizeDescription(String description) {
        return (description != null && !description.isBlank()) ? description.trim() : null;
    }
}
