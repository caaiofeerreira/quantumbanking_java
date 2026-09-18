package com.quantumbanking.modules.account.service.validation;

import com.quantumbanking.infra.exception.*;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.AccountType;
import com.quantumbanking.modules.account.repository.AccountRepository;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.client.domain.Company;
import com.quantumbanking.modules.client.service.validator.CompanyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountValidator {

    private final AccountRepository accountRepository;
    private final CompanyValidator companyValidator;

    public void validateAccount(AccountType accountType, Client client, Company company) {
        checkCompatibleAccountType(client.getType(), accountType);
        checkDuplicateAccountType(client, accountType);
        companyValidator.checkCompanyRequiredForAccount(client.getType(), company);
    }

    private void checkCompatibleAccountType(ClientType clientType, AccountType accountType) {

        if (clientType == ClientType.JURIDICA && accountType == AccountType.POUPANCA) {
            throw new InvalidAccountTypeException("Clientes do tipo Jurídica devem possuir uma conta do tipo CORRENTE.");
        }
    }

    private void checkDuplicateAccountType(Client client, AccountType accountType) {
        if (accountRepository.existsByClientIdAndType(client.getId(), accountType)) {
            throw new DuplicateAccountTypeException("Cliente já possui uma conta do tipo " + accountType.name() + ".");
        }
    }

    public void checkOwnership(Account account, Long userId) {
        if (!account.getClient().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Conta não pertence ao usuário autenticado.");
        }
    }
}