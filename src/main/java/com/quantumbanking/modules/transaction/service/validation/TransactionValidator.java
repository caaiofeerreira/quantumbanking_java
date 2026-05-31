package com.quantumbanking.modules.transaction.service.validation;

import com.quantumbanking.infra.exception.*;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.AccountStatus;
import com.quantumbanking.modules.account.domain.AccountType;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionValidator {

    private static final BigDecimal MIN_VALUE = new BigDecimal("0.01");

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

    private void checkMinimumAmount(BigDecimal amount) {

        if (amount == null) {
            throw new InvalidTransactionValueException("O valor da transação é obrigatório e não pode ser nulo.");
        }

        if (amount.compareTo(MIN_VALUE) < 0) {
            throw new MinimumAmountException("O valor mínimo para esta operação é de R$ 0,01");
        }
    }

    private void checkAccountType(Account account) {
        if (account.getType().equals(AccountType.POUPANCA)){
            throw new TransactionNotAuthorizedException("Transferência não permitida. Contas poupança só podem realizar transações para a conta corrente do mesmo titular.");
        }
    }

    public void validateDeposit(Account account, BigDecimal amount) {
        checkAccountActive(account);
        checkMinimumAmount(amount);
    }

    public void validateWithdraw(Account account, BigDecimal amount) {
        checkAccountActive(account);
        checkMinimumAmount(amount);
    }

    public void validateInternal(Account originAccount, Account destinationAccount, Agency agency) {
        checkDifferentAccounts(originAccount, destinationAccount);
        checkAccountType(originAccount);

        if (!destinationAccount.getAgency().equals(agency)) {
            throw new AgencyAccountMismatchException("A agência informada não coincide com a conta de destino.");
        }

    }

    public void validateExternal(Account account, String destinationAccount, String bankingCode) {
        checkAccountType(account);

        if (account.getAccountNumber().equals(destinationAccount)
                && bankingCode.equals(compe)) {
            throw new TransactionNotAuthorizedException("Não é possível transferir para a própria conta.");
        }
    }

    public void validatePix(Account originAccount, Account destinationAccount) {
        checkAccountActive(originAccount);

        if (destinationAccount != null) {
            checkAccountActive(destinationAccount);
            checkDifferentAccounts(originAccount, destinationAccount);
        }
    }
}