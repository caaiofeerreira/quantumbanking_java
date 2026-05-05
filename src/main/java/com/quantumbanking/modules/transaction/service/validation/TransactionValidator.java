package com.quantumbanking.modules.transaction.service.validation;

import com.quantumbanking.infra.exception.AgencyAccountMismatchException;
import com.quantumbanking.infra.exception.TransactionNotAuthorizedException;
import com.quantumbanking.infra.exception.ValidateException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.AccountStatus;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TransactionValidator {

    @Value("${bank.code}")
    private String bankCode;

    private void isAccountActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new ValidateException("A conta " + account.getId() + " não está ativa.");
        }
    }

    private void isNotSameAccount(Account originAccount, Account destinyAccount) {

        if (originAccount == null || destinyAccount == null) {
            return;
        }

        if (originAccount.getId().equals(destinyAccount.getId())) {
            throw new TransactionNotAuthorizedException("Não é possível enviar dinheiro para si mesmo.");
        }
    }

    public void validateDeposit(Account account) {
        isAccountActive(account);
    }

    public void validateWithdraw(Account account) {
        isAccountActive(account);
    }

    public void validateInternal(Account originAccount, Account destinyAccount, Agency agency) {
        isNotSameAccount(originAccount, destinyAccount);

        if (!destinyAccount.getAgency().equals(agency)) {
            throw new AgencyAccountMismatchException("A agência informada não coincide com a conta de destino.");
        }

    }

    public void validateExternal(Account account, String accountDestiny, String bankingCode) {
        if (account.getAccountNumber().equals(accountDestiny)
                && bankingCode.equals(bankCode)) {
            throw new TransactionNotAuthorizedException("Não é possível transferir para a própria conta.");
        }
    }

    public void validatePix(Account originAccount, Account destinyAccount) {
        isAccountActive(originAccount);

        if (destinyAccount != null) {
            isAccountActive(destinyAccount);
            isNotSameAccount(originAccount, destinyAccount);
        }
    }
}