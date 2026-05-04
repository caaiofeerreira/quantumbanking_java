package com.quantumbanking.modules.transaction.mapper;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.dto.*;
import com.quantumbanking.modules.transaction.formater.TransactionStatementFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final TransactionStatementFormatter transactionStatementFormatter;

    public DepositResponseDTO toDepositResponse(Transaction transaction) {
        return new DepositResponseDTO(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getCreatedAt()
        );
    }

    public WithdrawResponseDTO toWithdrawResponse(Transaction transaction) {
        return new WithdrawResponseDTO(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getCreatedAt()
        );
    }

    public InternalTransactionResponseDTO toInternalResponse(Transaction transaction) {
        return new InternalTransactionResponseDTO(
                transaction.getId(),
                formatAccountNumber(transaction.getAccountDestiny().getAccountNumber()),
                transaction.getAccountDestiny().getClient().getName(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDestinyAgency());
    }

    public ExternalTransactionResponseDTO toExternalResponse(Transaction transaction) {
        return new ExternalTransactionResponseDTO(
                transaction.getId(),
                transaction.getDestinyName(),
                transaction.getDestinyAccount(),
                transaction.getDestinyAgency(),
                transaction.getBankCode(),
                transaction.getDestinyDocument(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getCreatedAt()
        );
    }

    public PixTransactionResponseDTO toPixResponse(Transaction transaction) {
        return new PixTransactionResponseDTO(
                transaction.getId(),
                transaction.getAccountDestiny() != null
                        ? transaction.getAccountDestiny().getClient().getName()
                        : null,
                transaction.getDestinyDocument(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getCreatedAt(),
                transaction.getDescription()
        );
    }

    public TransactionStatementDTO toStatementResponse(Transaction transaction, Account account) {
        Long accountId = account.getId();

        boolean isOrigin = transaction.isSentBy(accountId);
        boolean isDestiny = transaction.isReceivedBy(accountId);

        return new TransactionStatementDTO(
                transaction.getId(),
                transaction.getType(),
                isOrigin ? transaction.getAmount().negate() : transaction.getAmount(),
                transactionStatementFormatter.getDisplayDescription(transaction, isOrigin),
                transactionStatementFormatter.getCounterpartName(transaction, isOrigin),
                transaction.getCreatedAt()
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