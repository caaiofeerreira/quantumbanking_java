package com.quantumbanking.modules.transaction.factory;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.loan.domain.Loan;
import com.quantumbanking.modules.pixKey.domain.PixKeyType;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.domain.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionFactory {

    private String normalizeDescription(String description) {
        return (description != null && !description.isBlank()) ? description.trim() : null;
    }

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

    public Transaction createFee(Account account, Bank bank , BigDecimal amount) {
        return Transaction.builder()
                .originAccount(account)
                .originName(account.getClient().getName())
                .originAccountNumber(account.getAccountNumber())
                .originAgency(account.getAgency().getAgencyNumber())
                .originBankCompe(account.getAgency().getBank().getCompe())
                .originDocument(account.getClient().getCpf())
                .amount(amount)
                .type(TransactionType.FEE)
                .bankAccount(bank.getAccount())
                .destinationBankName(bank.getName())
                .destinationBankCompe(bank.getCompe())
                .destinationAccountNumber(bank.getAccount().getAccountNumber())
                .destinationAgency(bank.getAccount().getAgencyNumber())
                .description("Tarifa por excesso de saques no mês")
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
                .destinationBankName(destinationAccount.getAgency().getBank().getName())


                .amount(amount)
                .type(TransactionType.INTERNAL_TRANSFER)
                .description(normalizeDescription(description))
                .build();
    }

    public Transaction createExternalTransfer(Account originAccount, String destinationAccountNumber, String destinationName, String destinationAgency,
                                              String compe, String destinationDocument, String bankName, BigDecimal amount, String description) {
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
                .destinationBankName(bankName)

                .amount(amount)
                .type(TransactionType.EXTERNAL_TRANSFER)
                .description(normalizeDescription(description))
                .build();
    }

    public Transaction createPix(Account originAccount, Account destinationAccount, BigDecimal amount,
                                 String description, String pixKey, PixKeyType pixKeyType) {
        var builder = Transaction.builder()
                .originAccount(originAccount)
                .originName(originAccount.getClient().getName())
                .originAccountNumber(originAccount.getAccountNumber())
                .originAgency(originAccount.getAgency().getAgencyNumber())
                .originBankCompe(originAccount.getAgency().getBank().getCompe())
                .originDocument(originAccount.getClient().getCpf())

                .amount(amount)
                .pixKey(pixKey)
                .pixKeyType(pixKeyType)
                .type(TransactionType.PIX)
                .description(normalizeDescription(description));

        if (destinationAccount != null) {
            builder.destinationAccount(destinationAccount)
                    .destinationName(destinationAccount.getClient().getName())
                    .destinationAccountNumber(destinationAccount.getAccountNumber())
                    .destinationAgency(destinationAccount.getAgency().getAgencyNumber())
                    .destinationBankCompe(destinationAccount.getAgency().getBank().getCompe())
                    .destinationDocument(destinationAccount.getClient().getCpf())
                    .destinationBankName(destinationAccount.getAgency().getBank().getName());
        }

        return builder.build();
    }

    public Transaction createLoan(Loan loan) {

        Bank bank = loan.getAccount().getAgency().getBank();
        Account account = loan.getAccount();

        return Transaction.builder()
                .originName(bank.getName())
                .originBankCompe(bank.getCompe())
                .originAccountNumber(bank.getAccount().getAccountNumber())
                .originAgency(bank.getAccount().getAgencyNumber())
                .bankAccount(bank.getAccount())

                .destinationAccount(account)
                .destinationName(account.getClient().getName())
                .destinationAccountNumber(account.getAccountNumber())
                .destinationAgency(account.getAgency().getAgencyNumber())
                .destinationBankCompe(account.getAgency().getBank().getCompe())
                .destinationDocument(account.getClient().getCpf())

                .loan(loan)
                .amount(loan.getAmount())
                .type(TransactionType.LOAN)
                .description(normalizeDescription(loan.getDescription()))
                .build();
    }
}