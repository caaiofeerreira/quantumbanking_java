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
                transferDestinationInfo(transaction),
                transferOriginInfo(transaction.getOriginAccount())
        );
    }

    public ExternalTransactionResponseDTO toExternalResponse(Transaction transaction) {
        return new ExternalTransactionResponseDTO(
                transaction.getId(),
                transaction.getCreatedAt(),
                transaction.getType(),
                transaction.getAmount(),
                transferDestinationInfo(transaction),
                transferOriginInfo(transaction.getOriginAccount())
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
                pixDestinationInfo(transaction),
                pixOriginInfo(transaction.getOriginAccount())
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

    private AccountInfoDTO transferOriginInfo(Account originAccount) {
        return new AccountInfoDTO(
                originAccount.getClient().getName(),
                originAccount.getClient().getCpf(),
                originAccount.getAgency().getBank().getName(),
                originAccount.getAgency().getAgencyNumber(),
                formatAccountNumber(originAccount.getAccountNumber()),
                originAccount.getAgency().getBank().getCompe()
        );
    }

    private AccountInfoDTO transferDestinationInfo(Transaction transaction) {
        if (transaction.getDestinationAccount() != null) {
            return new AccountInfoDTO(
                    transaction.getDestinationAccount().getClient().getName(),
                    transaction.getDestinationAccount().getClient().getCpf(),
                    transaction.getDestinationAccount().getAgency().getBank().getName(),
                    transaction.getDestinationAccount().getAgency().getAgencyNumber(),
                    formatAccountNumber(transaction.getDestinationAccount().getAccountNumber()),
                    transaction.getDestinationAccount().getAgency().getBank().getCompe()
            );
        }
        return new AccountInfoDTO(
                transaction.getDestinationName(),
                transaction.getDestinationDocument(),
                transaction.getDestinationBankName(),
                transaction.getDestinationAgency(),
                formatAccountNumber(transaction.getDestinationAccountNumber()),
                transaction.getDestinationBankCompe()
        );
    }

    private PixAccountInfoDTO pixOriginInfo(Account originAccount) {
        return new PixAccountInfoDTO(
                originAccount.getClient().getName(),
                originAccount.getClient().getCpf(),
                originAccount.getAgency().getBank().getName()
        );
    }

    private PixAccountInfoDTO pixDestinationInfo(Transaction transaction) {
        if (transaction.getDestinationAccount() != null) {
            return new PixAccountInfoDTO(
                    transaction.getDestinationAccount().getClient().getName(),
                    transaction.getDestinationAccount().getClient().getCpf(),
                    transaction.getDestinationAccount().getAgency().getBank().getName()
            );
        }

        return new PixAccountInfoDTO(
                defaultIfEmpty(transaction.getDestinationName(), "Titular não identificado"),
                defaultIfEmpty(transaction.getDestinationDocument(), "Documento não informado"),
                defaultIfEmpty(transaction.getDestinationBankName(), "Instituição Externa")
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