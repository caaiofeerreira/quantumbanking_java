package com.quantumbanking.modules.transaction.service.validation;

import com.quantumbanking.infra.exception.*;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.AccountStatus;
import com.quantumbanking.modules.account.domain.AccountType;
import com.quantumbanking.modules.shared.util.FormattingUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;

@Component
public class TransactionValidator {

    private static final BigDecimal MIN_TRANSACTION_VALUE = new BigDecimal("0.01");
    private static final LocalTime NIGHTTIME_START = LocalTime.of(18, 0);
    private static final LocalTime NIGHTTIME_END   = LocalTime.of(6, 0);

    @Value("${transaction.nighttime-limit}")
    private BigDecimal nighttimeLimit;

    @Value("${transaction.max-atm-amount}")
    private BigDecimal maxAtmAmount;

    @Value("${bank.compe}")
    private String compe;

    private void checkAccountActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountStatusException("A conta " + account.getId() + " não está ativa.");
        }
    }

    private void checkDifferentAccounts(Account originAccount, Account destinationAccount) {
        if (originAccount == null || destinationAccount == null) {
            return;
        }

        if (originAccount.getId().equals(destinationAccount.getId())) {
            throw new TransactionNotAuthorizedException("Não é possível enviar dinheiro para si mesmo.");
        }
    }

    private void checkMinimumTransactionAmount(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidTransactionValueException("O valor da transação é obrigatório e não pode ser nulo.");
        }

        if (amount.compareTo(MIN_TRANSACTION_VALUE) < 0) {
            throw new MinimumAmountException("O valor mínimo para esta operação é de R$ 0,01");
        }
    }

    private void checkATMMaximumAmount(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidTransactionValueException("O valor é obrigatório e não pode ser nulo.");
        }

        if (amount.compareTo(maxAtmAmount) > 0) {
            throw new MaximumAmountException("O valor máximo para saque/depósito é de R$ 5.000,00.");
        }
    }

    private void checkSavingsAccountInternal(Account originAccount, Account destinationAccount) {
        if (originAccount.getType().equals(AccountType.POUPANCA)) {
            if (!destinationAccount.getClient().getId().equals(originAccount.getClient().getId())) {
                throw new TransactionNotAuthorizedException(
                        "Transferência não permitida. Contas poupança só podem realizar transferências para contas do mesmo titular.");
            }
        }
    }

    private void checkSavingsAccountExternal(Account account) {
        if (account.getType().equals(AccountType.POUPANCA)) {
            throw new TransactionNotAuthorizedException(
                    "Transferência não permitida. Contas poupança não podem realizar transferências externas.");
        }
    }

    private void checkPixAuthorized(BigDecimal amount, LocalTime transactionTime) {
        boolean isNighttime = !transactionTime.isBefore(NIGHTTIME_START) || transactionTime.isBefore(NIGHTTIME_END);

        if (isNighttime && amount.compareTo(nighttimeLimit) > 0) {
            throw new TransactionNotAuthorizedException(
                    "Transação recusada: Valor acima do limite noturno de R$ 1000,00."
            );
        }
    }

    private void checkAccountOwnership(Account account, Long userId) {
        if (!account.getClient().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Conta não pertence ao usuário autenticado.");
        }
    }

    private void checkDestinationDocument(String document) {
        boolean isValid = FormattingUtils.isValidCpf(document) || FormattingUtils.isValidCnpj(document);

        if (!isValid) {
            throw new InvalidDocumentException("O documento informado para a conta de destino é inválido. Verifique o número digitado e tente novamente.");
        }
    }

    public void validateDeposit(Account account, BigDecimal amount) {
        checkAccountActive(account);
        checkATMMaximumAmount(amount);
    }

    public void validateWithdraw(Account account, BigDecimal amount, boolean shouldChargeFee) {
        checkAccountActive(account);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionValueException("O valor é obrigatório e deve ser positivo.");
        }

        BigDecimal feeAmount = shouldChargeFee ? account.getType().getFeeAmount() : BigDecimal.ZERO;
        account.ensureSufficientBalance(amount.add(feeAmount));
    }

    public void validateInternal(Account originAccount, Account destinationAccount, Long agencyId, BigDecimal amount, Long userId) {
        checkAccountOwnership(originAccount, userId);
        checkDifferentAccounts(originAccount, destinationAccount);
        checkSavingsAccountInternal(originAccount, destinationAccount);
        checkMinimumTransactionAmount(amount);

        if (!destinationAccount.getAgency().getId().equals(agencyId)) {
            throw new AgencyAccountMismatchException("A agência informada não coincide com a conta de destino.");
        }
    }

    public void validateExternal(Account account, String bankingCode, BigDecimal amount, Long userId, String document) {
        checkAccountOwnership(account, userId);
        checkSavingsAccountExternal(account);
        checkMinimumTransactionAmount(amount);
        checkDestinationDocument(document);

        if (bankingCode.equals(compe)) {
            throw new TransactionNotAuthorizedException(
                    "Para transferências entre contas do Quantum Banking, utilize a transferência interna.");
        }
    }

    public void validatePix(Account originAccount, Account destinationAccount, BigDecimal amount, LocalTime time, Long userId) {
        checkAccountOwnership(originAccount, userId);
        checkAccountActive(originAccount);
        checkPixAuthorized(amount, time);
        checkMinimumTransactionAmount(amount);

        if (destinationAccount != null) {
            checkAccountActive(destinationAccount);
            checkDifferentAccounts(originAccount, destinationAccount);
        }
    }
}