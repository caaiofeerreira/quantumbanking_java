package com.quantumbanking.modules.transaction.mapper;

import com.quantumbanking.infra.exception.TransactionOwnershipMismatchException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.loan.domain.Loan;
import com.quantumbanking.modules.pixKey.domain.PixKeyType;
import com.quantumbanking.modules.shared.util.DataMaskingUtils;
import com.quantumbanking.modules.shared.util.FormattingUtils;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.dto.*;
import com.quantumbanking.modules.transaction.formater.TransactionStatementFormatter;
import com.quantumbanking.modules.transaction.resolver.AccountHolderInfoResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final TransactionStatementFormatter transactionStatementFormatter;
    private final AccountHolderInfoResolver accountHolderInfoResolver;

    private String maskIdentifierIfNeeded(Transaction transaction) {

        if (transaction.getPixKeyType() == null) return null;

        PixKeyType type = transaction.getPixKeyType();
        String key = transaction.getPixKey();

        return switch (type) {
            case CPF -> DataMaskingUtils.maskCpf(key);
            case CNPJ -> DataMaskingUtils.maskCnpj(key);
            case PHONE -> DataMaskingUtils.maskPhone(key);
            case EMAIL -> DataMaskingUtils.maskEmail(key);
            case RANDOM -> key;
        };
    }

    public DepositResponseDTO toDepositResponse(Transaction transaction) {
        return new DepositResponseDTO(
                transaction.getId(),
                transaction.getCreatedAt(),
                transaction.getType(),
                transaction.getAmount()
        );
    }

    public WithdrawResponseDTO toWithdrawResponse(Transaction transaction, FeeDetailDTO fee) {
        return new WithdrawResponseDTO(
                transaction.getId(),
                transaction.getCreatedAt(),
                transaction.getType(),
                transaction.getAmount(),
                fee
        );
    }

    public WithdrawResponseDTO toWithdrawResponse(Transaction transaction) {
        return toWithdrawResponse(transaction, null);
    }

    public InternalTransactionResponseDTO toInternalResponse(Transaction transaction) {
        return new InternalTransactionResponseDTO(
                transaction.getId(),
                transaction.getCreatedAt(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getFailureReason(),
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
                transaction.getStatus(),
                transaction.getFailureReason(),
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
                transaction.getStatus(),
                transaction.getFailureReason(),
                transaction.getAmount(),
                maskIdentifierIfNeeded(transaction),
                transaction.getPixKeyType(),
                transaction.getDescription(),
                pixDestinationInfo(transaction),
                pixOriginInfo(transaction.getOriginAccount())
        );
    }

    public LoanTransactionDetailDTO toLoanTransactionDetail(Transaction transaction) {
        Loan loan = transaction.getLoan();
        return new LoanTransactionDetailDTO(
                transaction.getId(),
                transaction.getCreatedAt(),
                transaction.getAmount(),
                transaction.getDescription(),
                loan.getTotalAmount(),
                loan.getInstallmentAmount(),
                loan.getInstallments(),
                loan.getPaidInstallments(),
                loan.getInterestRate(),
                loan.getStartDate(),
                loan.getEndDate(),
                loan.getStatus()
        );
    }

    public TransactionStatementDTO toStatementResponse(Transaction transaction, Account account) {

        Long accountId = account.getId();
        boolean isOrigin = transaction.isSentBy(accountId);
        boolean isDestination = transaction.isReceivedBy(accountId);

        if (!isOrigin && !isDestination) {
            throw new TransactionOwnershipMismatchException("Transação " + transaction.getId() + " não pertence à conta " + accountId
                    + " nem como origem, nem como destino");
        }

        return new TransactionStatementDTO(
                transaction.getId(),
                transaction.getCreatedAt(),
                transaction.getType(),
                transaction.getStatus(),
                isOrigin ? transaction.getAmount().negate() : transaction.getAmount(),
                transactionStatementFormatter.getDisplayDescription(transaction, isOrigin),
                transactionStatementFormatter.getCounterpartName(transaction, isOrigin)
        );
    }

    private AccountInfoDTO transferOriginInfo(Account originAccount) {
        AccountHolderInfo holder = accountHolderInfoResolver.resolve(originAccount);
        return new AccountInfoDTO(
                holder.name(),
                DataMaskingUtils.maskDocument(holder.document()),
                originAccount.getAgency().getBank().getName(),
                originAccount.getAgency().getAgencyNumber(),
                FormattingUtils.formatAccountNumber(originAccount.getAccountNumber()),
                originAccount.getAgency().getBank().getCompe()
        );
    }

    private AccountInfoDTO transferDestinationInfo(Transaction transaction) {
        if (transaction.getDestinationAccount() != null) {
            AccountHolderInfo holder = accountHolderInfoResolver.resolve(transaction.getDestinationAccount());
            return new AccountInfoDTO(
                    holder.name(),
                    DataMaskingUtils.maskDocument(holder.document()),
                    transaction.getDestinationAccount().getAgency().getBank().getName(),
                    transaction.getDestinationAccount().getAgency().getAgencyNumber(),
                    FormattingUtils.formatAccountNumber(transaction.getDestinationAccount().getAccountNumber()),
                    transaction.getDestinationAccount().getAgency().getBank().getCompe()
            );
        }
        return new AccountInfoDTO(
                transaction.getDestinationName(),
                DataMaskingUtils.maskDocument(transaction.getDestinationDocument()),
                transaction.getDestinationBankName(),
                transaction.getDestinationAgency(),
                FormattingUtils.formatAccountNumber(transaction.getDestinationAccountNumber()),
                transaction.getDestinationBankCompe()
        );
    }

    private PixAccountInfoDTO pixOriginInfo(Account originAccount) {
        AccountHolderInfo holder = accountHolderInfoResolver.resolve(originAccount);
        return new PixAccountInfoDTO(
                holder.name(),
                DataMaskingUtils.maskDocument(holder.document()),
                originAccount.getAgency().getBank().getName()
        );
    }

    private PixAccountInfoDTO pixDestinationInfo(Transaction transaction) {
        if (transaction.getDestinationAccount() != null) {
            AccountHolderInfo holder = accountHolderInfoResolver.resolve(transaction.getDestinationAccount());
            return new PixAccountInfoDTO(
                    holder.name(),
                    DataMaskingUtils.maskDocument(holder.document()),
                    transaction.getDestinationAccount().getAgency().getBank().getName()
            );
        }

        return new PixAccountInfoDTO(
                defaultIfEmpty(transaction.getDestinationName(), "Titular não identificado"),
                defaultIfEmpty(DataMaskingUtils.maskDocument(transaction.getDestinationDocument()), "Documento não informado"),
                defaultIfEmpty(transaction.getDestinationBankName(), "Instituição Externa")
        );
    }
}