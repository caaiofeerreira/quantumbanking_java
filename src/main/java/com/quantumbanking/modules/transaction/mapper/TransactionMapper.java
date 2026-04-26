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
                transaction.getAccountDestiny().getAccountNumber(),
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
                transaction.getAccountDestiny() != null
                        ? transaction.getAccountDestiny().getAccountNumber()
                        : transaction.getDestinyDocument(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getCreatedAt()
        );
    }

    public TransactionStatementDTO toStatementResponse(Transaction transaction, Account account) {
        boolean isOrigin = transaction.getAccountOrigin() != null &&
                transaction.getAccountOrigin().getId().equals(account.getId());

        return new TransactionStatementDTO(
                transaction.getId(),
                transaction.getType(),
                isOrigin ? transaction.getAmount().negate() : transaction.getAmount(),
                transactionStatementFormatter.getDisplayDescription(transaction, isOrigin),
                transactionStatementFormatter.getCounterpartName(transaction, isOrigin),
                transaction.getCreatedAt()
        );
    }
}