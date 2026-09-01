package com.quantumbanking.modules.transaction.factory;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.loan.domain.Loan;
import com.quantumbanking.modules.pixKey.domain.PixKeyType;
import com.quantumbanking.modules.shared.util.FormattingUtils;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.domain.TransactionStatus;
import com.quantumbanking.modules.transaction.domain.TransactionType;
import com.quantumbanking.modules.transaction.dto.AccountHolderInfo;
import com.quantumbanking.modules.transaction.resolver.AccountHolderInfoResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TransactionFactory {

    private final AccountHolderInfoResolver accountHolderInfoResolver;

    private String normalizeDescription(String description) {
        return (description != null && !description.isBlank()) ? description.trim() : null;
    }

    public Transaction createDeposit(Account account, BigDecimal amount, String description, TransactionStatus status) {

        AccountHolderInfo accountHolder = accountHolderInfoResolver.resolve(account);

        return Transaction.builder()
                .destinationAccount(account)
                .destinationName(accountHolder.name())
                .destinationAccountNumber(account.getAccountNumber())
                .destinationAgency(account.getAgency().getAgencyNumber())
                .destinationBankCompe(account.getAgency().getBank().getCompe())
                .destinationDocument(FormattingUtils.normalizeDocument(accountHolder.document()))

                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .status(status)
                .description(normalizeDescription(description))
                .build();
    }

    public Transaction createWithdrawal(Account account, BigDecimal amount, String description, TransactionStatus status) {
        return createWithdrawal(account, amount, description, status, null);
    }

    public Transaction createWithdrawal(Account account, BigDecimal amount, String description, TransactionStatus status, Instant availableAt) {

        AccountHolderInfo accountHolder = accountHolderInfoResolver.resolve(account);

        return Transaction.builder()
                .originAccount(account)
                .originName(accountHolder.name())
                .originAccountNumber(account.getAccountNumber())
                .originAgency(account.getAgency().getAgencyNumber())
                .originBankCompe(account.getAgency().getBank().getCompe())
                .originDocument(FormattingUtils.normalizeDocument(accountHolder.document()))

                .amount(amount)
                .type(TransactionType.WITHDRAWAL)
                .status(status)
                .description(normalizeDescription(description))
                .availableAt(availableAt)
                .build();
    }

    public Transaction createFee(Account account, Bank bank , BigDecimal amount, TransactionStatus status) {

        AccountHolderInfo accountHolder = accountHolderInfoResolver.resolve(account);

        return Transaction.builder()
                .originAccount(account)
                .originName(accountHolder.name())
                .originAccountNumber(account.getAccountNumber())
                .originAgency(account.getAgency().getAgencyNumber())
                .originBankCompe(account.getAgency().getBank().getCompe())
                .originDocument(FormattingUtils.normalizeDocument(accountHolder.document()))
                .amount(amount)
                .type(TransactionType.FEE)
                .status(status)
                .bankAccount(bank.getAccount())
                .destinationBankName(bank.getName())
                .destinationBankCompe(bank.getCompe())
                .destinationAccountNumber(bank.getAccount().getAccountNumber())
                .destinationAgency(bank.getAccount().getAgencyNumber())
                .description("Tarifa por excesso de saques no mês")
                .build();
    }

    public Transaction createInternalTransfer(Account originAccount, Account destinationAccount, String destinationAgencyNumber, BigDecimal amount, String description, TransactionStatus status) {

        AccountHolderInfo originHolder = accountHolderInfoResolver.resolve(originAccount);
        AccountHolderInfo destinationHolder = accountHolderInfoResolver.resolve(destinationAccount);

        return Transaction.builder()
                .originAccount(originAccount)
                .originName(originHolder.name())
                .originAccountNumber(originAccount.getAccountNumber())
                .originAgency(originAccount.getAgency().getAgencyNumber())
                .originBankCompe(originAccount.getAgency().getBank().getCompe())
                .originDocument(FormattingUtils.normalizeDocument(originHolder.document()))

                .destinationAccount(destinationAccount)
                .destinationName(destinationHolder.name())
                .destinationAgency(destinationAgencyNumber)
                .destinationAccountNumber(destinationAccount.getAccountNumber())
                .destinationBankCompe(destinationAccount.getAgency().getBank().getCompe())
                .destinationDocument(FormattingUtils.normalizeDocument(destinationHolder.document()))
                .destinationBankName(destinationAccount.getAgency().getBank().getName())


                .amount(amount)
                .type(TransactionType.INTERNAL_TRANSFER)
                .status(status)
                .description(normalizeDescription(description))
                .build();
    }

    public Transaction createExternalTransfer(Account originAccount, String destinationAccountNumber, String destinationName, String destinationAgency,
                                              String compe, String destinationDocument, String bankName, BigDecimal amount, String description, TransactionStatus status) {

        AccountHolderInfo originHolder = accountHolderInfoResolver.resolve(originAccount);

        return Transaction.builder()
                .originAccount(originAccount)
                .originName(originHolder.name())
                .originAccountNumber(originAccount.getAccountNumber())
                .originAgency(originAccount.getAgency().getAgencyNumber())
                .originBankCompe(originAccount.getAgency().getBank().getCompe())
                .originDocument(FormattingUtils.normalizeDocument(originHolder.document()))

                .destinationAccountNumber(destinationAccountNumber)
                .destinationName(destinationName)
                .destinationAgency(destinationAgency)
                .destinationBankCompe(compe)
                .destinationDocument(FormattingUtils.normalizeDocument(destinationDocument))
                .destinationBankName(bankName)

                .amount(amount)
                .type(TransactionType.EXTERNAL_TRANSFER)
                .status(status)
                .description(normalizeDescription(description))
                .build();
    }

    public Transaction createPix(Account originAccount, Account destinationAccount, BigDecimal amount,
                                 String description, String pixKey, PixKeyType pixKeyType,
                                 TransactionStatus status, String externalBankCompe,
                                 String externalBankName, String externalDocument, String externalName) {

        AccountHolderInfo originHolder = accountHolderInfoResolver.resolve(originAccount);

        var builder = Transaction.builder()
                .originAccount(originAccount)
                .originName(originHolder.name())
                .originAccountNumber(originAccount.getAccountNumber())
                .originAgency(originAccount.getAgency().getAgencyNumber())
                .originBankCompe(originAccount.getAgency().getBank().getCompe())
                .originDocument(FormattingUtils.normalizeDocument(originHolder.document()))
                .amount(amount)
                .pixKey(pixKey)
                .pixKeyType(pixKeyType)
                .type(TransactionType.PIX)
                .status(status)
                .description(normalizeDescription(description));

        if (destinationAccount != null) {

            AccountHolderInfo destinationHolder = accountHolderInfoResolver.resolve(destinationAccount);

            builder.destinationAccount(destinationAccount)
                    .destinationName(destinationHolder.name())
                    .destinationAccountNumber(destinationAccount.getAccountNumber())
                    .destinationAgency(destinationAccount.getAgency().getAgencyNumber())
                    .destinationBankCompe(destinationAccount.getAgency().getBank().getCompe())
                    .destinationDocument(FormattingUtils.normalizeDocument(destinationHolder.document()))
                    .destinationBankName(destinationAccount.getAgency().getBank().getName());
        } else {
            builder.destinationBankCompe(externalBankCompe)
                    .destinationBankName(externalBankName)
                    .destinationDocument(FormattingUtils.normalizeDocument(externalDocument))
                    .destinationName(externalName);
        }

        return builder.build();
    }

    public Transaction createLoan(Loan loan, TransactionStatus status) {

        Bank bank = loan.getAccount().getAgency().getBank();
        Account account = loan.getAccount();

        AccountHolderInfo accountHolder = accountHolderInfoResolver.resolve(account);

        return Transaction.builder()
                .originName(bank.getName())
                .originBankCompe(bank.getCompe())
                .originAccountNumber(bank.getAccount().getAccountNumber())
                .originAgency(bank.getAccount().getAgencyNumber())
                .bankAccount(bank.getAccount())

                .destinationAccount(account)
                .destinationName(accountHolder.name())
                .destinationAccountNumber(account.getAccountNumber())
                .destinationAgency(account.getAgency().getAgencyNumber())
                .destinationBankCompe(account.getAgency().getBank().getCompe())
                .destinationDocument(FormattingUtils.normalizeDocument(accountHolder.document()))

                .loan(loan)
                .amount(loan.getAmount())
                .type(TransactionType.LOAN)
                .status(status)
                .description(normalizeDescription(loan.getDescription()))
                .build();
    }
}