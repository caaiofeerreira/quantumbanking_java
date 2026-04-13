package com.quantumbanking.modules.transaction.mapper;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.domain.TransactionType;
import com.quantumbanking.modules.transaction.dto.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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

        boolean origin = transaction.getAccountOrigin() != null &&
                transaction.getAccountOrigin().getId().equals(userAccount.getId());

        BigDecimal amount = origin ? transaction.getAmount().negate() : transaction.getAmount();

        String displayDescription = transaction.getDescription();

        if (displayDescription == null || displayDescription.isBlank()) {
            displayDescription = switch (transaction.getType()) {
                case TRANSFER_INTERNAL, TRANSFER_EXTERNAL -> origin ? "Transferência Enviada" : "Transferência Recebida";
                case PIX -> origin ? "Pix Enviado" : "Pix Recebido";
                case SAQUE -> "Saque";
                case DEPOSITO -> "Depósito";
                default -> transaction.getType().name();
            };
        }

        String counterpartName = null;

        if (transaction.getType() == TransactionType.SAQUE) {
            counterpartName = "Caixa Eletrônico";
        } else if (transaction.getType() == TransactionType.DEPOSITO) {
            counterpartName = "Depósito em Espécie";
        } else if (origin) {
            counterpartName = (transaction.getAccountDestiny() != null)
                    ? transaction.getAccountDestiny().getClient().getName()
                    : transaction.getDestinyName();
        } else {
            counterpartName = (transaction.getAccountOrigin() != null)
                    ? transaction.getAccountOrigin().getClient().getName()
                    : "Origem Externa";
        }

        return new TransactionStatementDTO(
                transaction.getId(),
                transaction.getType(),
                amount,
                displayDescription,
                counterpartName,
                transaction.getCreatedAt()
        );
    }
}