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
                .destinationAccount(account)
                .destinationName(account.getClient().getName())
                .destinationAccountNumber(account.getAccountNumber())
                .destinationAgency(account.getAgency().getAgencyNumber())
                .destinationBankCompe(account.getAgency().getBank().getCompe())
                .destinationDocument(account.getClient().getCpf())

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
                .originBankCompe(account.getAgency().getBank().getCompe())
                .originDocument(account.getClient().getCpf())

                .amount(amount)
                .type(TransactionType.WITHDRAWAL)
                .description(normalizeDescription(description))
                .build();
    }

    public Transaction createInternalTransfer(Account originAccount, Account destinationAccount, String destinationAgencyNumber, BigDecimal amount, String description) {
        return Transaction.builder()
                .originAccount(originAccount)
                .originName(originAccount.getClient().getName())
                .originAccountNumber(originAccount.getAccountNumber())
                .originAgency(originAccount.getAgency().getAgencyNumber())
                .originBankCompe(originAccount.getAgency().getBank().getCompe())
                .originDocument(originAccount.getClient().getCpf())

                .destinationAccount(destinationAccount)
                .destinationName(destinationAccount.getClient().getName())
                .destinationAgency(destinationAgencyNumber)
                .destinationAccountNumber(destinationAccount.getAccountNumber())
                .destinationBankCompe(destinationAccount.getAgency().getBank().getCompe())
                .destinationDocument(destinationAccount.getClient().getCpf())


                .amount(amount)
                .type(TransactionType.INTERNAL_TRANSFER)
                .description(normalizeDescription(description))
                .build();
    }

    public Transaction createExternalTransfer(Account originAccount, String destinationAccountNumber, String destinationName, String destinationAgency,
                                              String compe, String destinationDocument, BigDecimal amount, String description) {
        return Transaction.builder()
                .originAccount(originAccount)
                .originName(originAccount.getClient().getName())
                .originAccountNumber(originAccount.getAccountNumber())
                .originAgency(originAccount.getAgency().getAgencyNumber())
                .originBankCompe(originAccount.getAgency().getBank().getCompe())
                .originDocument(originAccount.getClient().getCpf())

                .destinationAccountNumber(destinationAccountNumber)
                .destinationName(destinationName)
                .destinationAgency(destinationAgency)
                .destinationBankCompe(compe)
                .destinationDocument(destinationDocument)

                .amount(amount)
                .type(TransactionType.EXTERNAL_TRANSFER)
                .description(normalizeDescription(description))
                .build();
    }

    public Transaction createPix(Account originAccount, Account destinationAccount, BigDecimal amount, String description, String pixKey) {
        var builder = Transaction.builder()
                .originAccount(originAccount)
                .originName(originAccount.getClient().getName())
                .originAccountNumber(originAccount.getAccountNumber())
                .originAgency(originAccount.getAgency().getAgencyNumber())
                .originBankCompe(originAccount.getAgency().getBank().getCompe())
                .originDocument(originAccount.getClient().getCpf())

                .amount(amount)
                .pixKey(pixKey)
                .type(TransactionType.PIX)
                .description(normalizeDescription(description));

        if (destinationAccount != null) {
            builder.destinationAccount(destinationAccount)
                    .destinationName(destinationAccount.getClient().getName())
                    .destinationAccountNumber(destinationAccount.getAccountNumber())
                    .destinationAgency(destinationAccount.getAgency().getAgencyNumber())
                    .destinationBankCompe(destinationAccount.getAgency().getBank().getCompe())
                    .destinationDocument(destinationAccount.getClient().getCpf());
        }

        return builder.build();
    }

    public Transaction createLoan(Bank bank, Account account, BigDecimal amount, String description) {
        return Transaction.builder()
                .originName(bank.getName())
                .originBankCompe(bank.getCompe())

                .destinationAccount(account)
                .destinationName(account.getClient().getName())
                .destinationAccountNumber(account.getAccountNumber())
                .destinationAgency(account.getAgency().getAgencyNumber())
                .destinationBankCompe(account.getAgency().getBank().getCompe())
                .destinationDocument(account.getClient().getCpf())

                .amount(amount)
                .type(TransactionType.LOAN)
                .description(normalizeDescription(description))
                .build();
    }

    private String normalizeDescription(String description) {
        return (description != null && !description.isBlank()) ? description.trim() : null;
    }
}