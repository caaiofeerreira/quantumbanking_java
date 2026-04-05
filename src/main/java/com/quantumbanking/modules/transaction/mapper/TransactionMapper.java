package com.quantumbanking.modules.transaction.mapper;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.dto.*;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public DepositResponseDTO toDepositResponse(Transaction transaction) {
        return new DepositResponseDTO(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }

    public WithdrawResponseDTO toWithdrawResponse(Transaction transaction) {
        return new WithdrawResponseDTO(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDescription(),
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
                transaction.getDestinyAgency(),
                transaction.getDescription()
        );
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
                transaction.getDescription(),
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

    public TransactionStatementDTO toStatementResponse(Transaction transaction, Account userAccount) {

        String counterpartName = null;

        if (transaction.getAccountOrigin() != null &&
                !transaction.getAccountOrigin().getId().equals(userAccount.getId())) {
            counterpartName = transaction.getAccountOrigin().getClient().getName();
        } else if (transaction.getAccountDestiny() != null &&
                !transaction.getAccountDestiny().getId().equals(userAccount.getId())) {
            counterpartName = transaction.getAccountDestiny().getClient().getName();
        } else if (transaction.getDestinyName() != null) {
            counterpartName = transaction.getDestinyName();
        }

        return new TransactionStatementDTO(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                counterpartName,
                transaction.getCreatedAt()
        );
    }

}
