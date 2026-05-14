package com.quantumbanking.modules.transaction.mapper;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.dto.*;
import com.quantumbanking.modules.transaction.formater.TransactionStatementFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final TransactionStatementFormatter transactionStatementFormatter;

    public DepositResponseDTO toDepositResponse(Transaction transaction) {
        return new DepositResponseDTO(
                transaction.getId(),
                transaction.getCreatedAt(),
                transaction.getType(),
                transaction.getAmount()
        );
    }

    public WithdrawResponseDTO toWithdrawResponse(Transaction transaction) {
        return new WithdrawResponseDTO(
                transaction.getId(),
                transaction.getCreatedAt(),
                transaction.getType(),
                transaction.getAmount()
        );
    }

    public InternalTransactionResponseDTO toInternalResponse(Transaction transaction) {
        return new InternalTransactionResponseDTO(
                transaction.getId(),
                transaction.getCreatedAt(),
                transaction.getType(),
                transaction.getAmount(),
                originInfo(transaction.getOriginAccount()),
                destinyInfo(transaction)
        );
    }

    public ExternalTransactionResponseDTO toExternalResponse(Transaction transaction) {
        return new ExternalTransactionResponseDTO(
                transaction.getId(),
                transaction.getCreatedAt(),
                transaction.getType(),
                transaction.getAmount(),
                originInfo(transaction.getOriginAccount()),
                destinyInfo(transaction)
        );
    }

    public PixTransactionResponseDTO toPixResponse(Transaction transaction) {
        return new PixTransactionResponseDTO(
                transaction.getId(),
                transaction.getCreatedAt(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getPixKey(),
                transaction.getDescription(),
                originInfo(transaction.getOriginAccount()),
                destinyInfo(transaction)
        );
    }

    public TransactionStatementDTO toStatementResponse(Transaction transaction, Account account) {

        Long accountId = account.getId();
        boolean isOrigin = transaction.isSentBy(accountId);

        return new TransactionStatementDTO(
                transaction.getId(),
                transaction.getCreatedAt(),
                transaction.getType(),
                isOrigin ? transaction.getAmount().negate() : transaction.getAmount(),
                transactionStatementFormatter.getDisplayDescription(transaction, isOrigin),
                transactionStatementFormatter.getCounterpartName(transaction, isOrigin)
        );
    }

    private AccountInfoDTO originInfo(Account originAccount) {
        return new AccountInfoDTO(
                originAccount.getClient().getName(),
                originAccount.getClient().getCpf(),
                originAccount.getAgency().getBank().getName(),
                originAccount.getAgency().getAgencyNumber(),
                formatAccountNumber(originAccount.getAccountNumber())
        );
    }

    private AccountInfoDTO destinyInfo(Transaction transaction) {
        if (transaction.getDestinyAccount() != null) {
            return new AccountInfoDTO(
                    transaction.getDestinyAccount().getClient().getName(),
                    transaction.getDestinyAccount().getClient().getCpf(),
                    transaction.getDestinyAccount().getAgency().getBank().getName(),
                    transaction.getDestinyAccount().getAgency().getAgencyNumber(),
                    formatAccountNumber(transaction.getDestinyAccount().getAccountNumber())
            );
        }
        return new AccountInfoDTO(
                defaultIfEmpty(transaction.getDestinyName(), "Titular não identificado"),
                defaultIfEmpty(transaction.getDestinyDocument(), "Documento não informado"),
                defaultIfEmpty(transaction.getDestinyBankCode(), "Instituição Externa"),
                defaultIfEmpty(transaction.getDestinyAgency(), "---"),
                defaultIfEmpty(formatAccountNumber(transaction.getDestinyAccountNumber()), "---")
        );
    }

    private String formatAccountNumber(String number) {
        if (number == null || number.length() < 2) {
            return number;
        }
        int splitIndex = number.length() - 1;
        return number.substring(0, splitIndex) + "-" + number.substring(splitIndex);
    }
}