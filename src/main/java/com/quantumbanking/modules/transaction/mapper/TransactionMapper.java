package com.quantumbanking.modules.transaction.mapper;

import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.dto.DepositResponseDTO;
import com.quantumbanking.modules.transaction.dto.ExternalTransactionResponseDTO;
import com.quantumbanking.modules.transaction.dto.InternalTransactionResponseDTO;
import com.quantumbanking.modules.transaction.dto.WithdrawResponseDTO;
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
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDestinyAgency()
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
                transaction.getType(),
                transaction.getCreatedAt()
        );
    }

}
