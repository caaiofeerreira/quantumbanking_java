package com.quantumbanking.modules.transaction.mapper;

import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.dto.DepositResponseDTO;
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

}
