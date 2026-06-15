package com.quantumbanking.modules.loan.service.validation;

import com.quantumbanking.infra.exception.AccountStatusException;
import com.quantumbanking.infra.exception.TransactionNotAuthorizedException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.AccountStatus;
import com.quantumbanking.modules.account.domain.AccountType;
import com.quantumbanking.modules.loan.domain.LoanStatus;
import com.quantumbanking.modules.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoanValidator {

    private final LoanRepository loanRepository;

    public void validateLoan(Account account) {
        checkAccountType(account);
        checkAccountDisabled(account);
        checkExistingActiveLoan(account);
    }

    private void checkAccountType(Account account) {

        if (account.getType().equals(AccountType.POUPANCA)) {
            throw new TransactionNotAuthorizedException("Solicitação de crédito não autorizada: " +
                    "A modalidade de empréstimo não é suportada para contas do tipo Poupança.");
        }
    }

    private void checkAccountDisabled(Account account) {

        if (account.getStatus().equals(AccountStatus.DISABLED)) {
            throw new AccountStatusException("Solicitação de crédito não autorizada: " +
                    "Sua conta está desabilitada.");
        }
    }

    private void checkExistingActiveLoan(Account account) {

        boolean hasActiveLoan = loanRepository.existsByAccountIdAndStatusIn(
                account.getId(),
                List.of(LoanStatus.REQUESTED, LoanStatus.APPROVED)
        );

        if (hasActiveLoan) {
            throw new TransactionNotAuthorizedException("Solicitação de crédito não autorizada: " +
                    "Já existe um empréstimo ativo ou em análise para esta conta.");
        }
    }
}