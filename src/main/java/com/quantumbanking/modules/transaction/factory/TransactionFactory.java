package com.quantumbanking.modules.transaction.factory;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.domain.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionFactory {

    public Transaction createDeposit(Account account, BigDecimal amount, String description) {
        return Transaction.builder()
                .destinyAccount(account)
                .destinyName(account.getClient().getName())
                .destinyAccountNumber(account.getAccountNumber())
                .destinyAgency(account.getAgency().getAgencyNumber())
                .destinyBankCode(account.getAgency().getBank().getCompe())
                .destinyDocument(account.getClient().getCpf())

                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .description(normalizeDescription(description))
                .build();
    }

    public Transaction createWithdrawal(Account account, BigDecimal amount, String description) {
        return Transaction.builder()
                .originAccount(account)
                .originName(account.getClient().getName())
                .originAccountNumber(account.getAccountNumber())
                .originAgency(account.getAgency().getAgencyNumber())
                .originBankCode(account.getAgency().getBank().getCompe())
                .originDocument(account.getClient().getCpf())

                .amount(amount)
                .type(TransactionType.WITHDRAWAL)
                .description(normalizeDescription(description))
                .build();
    }

    public Transaction createInternalTransfer(Account originAccount, Account destinyAccount, String destinyAgencyNumber, BigDecimal amount, String description) {
        return Transaction.builder()
                .originAccount(originAccount)
                .originName(originAccount.getClient().getName())
                .originAccountNumber(originAccount.getAccountNumber())
                .originAgency(originAccount.getAgency().getAgencyNumber())
                .originBankCode(originAccount.getAgency().getBank().getCompe())
                .originDocument(originAccount.getClient().getCpf())

                .destinyAccount(destinyAccount)
                .destinyName(destinyAccount.getClient().getName())
                .destinyAgency(destinyAgencyNumber)
                .destinyAccountNumber(destinyAccount.getAccountNumber())
                .destinyBankCode(destinyAccount.getAgency().getBank().getCompe())
                .destinyDocument(destinyAccount.getClient().getCpf())

                .amount(amount)
                .destinyBankCode(destinyAccount.getAgency().getBank().getCompe())
                .type(TransactionType.INTERNAL_TRANSFER)
                .description(normalizeDescription(description))
                .build();
    }

    public Transaction createExternalTransfer(Account originAccount, String destinyAccountNumber, String destinyName, String destinyAgency,
                                              String bankCode, String destinyDocument, BigDecimal amount, String description) {
        return Transaction.builder()
                .originAccount(originAccount)
                .originName(originAccount.getClient().getName())
                .originAccountNumber(originAccount.getAccountNumber())
                .originAgency(originAccount.getAgency().getAgencyNumber())
                .originBankCode(originAccount.getAgency().getBank().getCompe())
                .originDocument(originAccount.getClient().getCpf())

                .destinyAccountNumber(destinyAccountNumber)
                .destinyName(destinyName)
                .destinyAgency(destinyAgency)
                .destinyBankCode(bankCode)
                .destinyDocument(destinyDocument)

                .amount(amount)
                .type(TransactionType.EXTERNAL_TRANSFER)
                .description(normalizeDescription(description))
                .build();
    }

    public Transaction createPix(Account originAccount, BigDecimal amount, String description, String pixKey, Account destinyAccount) {
        var builder = Transaction.builder()
                .originAccount(originAccount)
                .originName(originAccount.getClient().getName())
                .originAccountNumber(originAccount.getAccountNumber())
                .originAgency(originAccount.getAgency().getAgencyNumber())
                .originBankCode(originAccount.getAgency().getBank().getCompe())
                .originDocument(originAccount.getClient().getCpf())

                .amount(amount)
                .pixKey(pixKey)
                .type(TransactionType.PIX)
                .description(normalizeDescription(description));

        if (destinyAccount != null) {
            builder.destinyAccount(destinyAccount)
                    .destinyName(destinyAccount.getClient().getName())
                    .destinyAccountNumber(destinyAccount.getAccountNumber())
                    .destinyAgency(destinyAccount.getAgency().getAgencyNumber())
                    .destinyBankCode(destinyAccount.getAgency().getBank().getCompe())
                    .destinyDocument(destinyAccount.getClient().getCpf());
        }

        return builder.build();
    }

    public Transaction createLoan(Bank bank, Account account, BigDecimal amount, String description) {
        return Transaction.builder()
                .originName(bank.getName())
                .originBankCode(bank.getCompe())

                .destinyAccount(account)
                .destinyName(account.getClient().getName())
                .destinyAccountNumber(account.getAccountNumber())
                .destinyAgency(account.getAgency().getAgencyNumber())
                .destinyBankCode(account.getAgency().getBank().getCompe())
                .destinyDocument(account.getClient().getCpf())

                .amount(amount)
                .type(TransactionType.LOAN)
                .description(normalizeDescription(description))
                .build();
    }

    private String normalizeDescription(String description) {
        return (description != null && !description.isBlank()) ? description.trim() : null;
    }
}