package com.quantumbanking.modules.account.service.validation;

import com.quantumbanking.infra.exception.*;
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

    public void validateAccount(ClientType clientType, AccountType accountType, Client client, Company company) {
        checkCompatibleAccountType(clientType, accountType);
        companyValidator.checkCompanyRequiredForAccount(accountType, company);
        checkDuplicateAccountType(client, accountType);
    }

    private void checkCompatibleAccountType(ClientType clientType, AccountType accountType) {
        if (clientType == ClientType.FISICA && accountType == AccountType.JURIDICA) {
            throw new IncompatibleAccountTypeException("Clientes do tipo Física não podem possuir uma conta do tipo Jurídica.");
        }

        if (clientType == ClientType.JURIDICA && accountType != AccountType.JURIDICA) {
            throw new InvalidAccountTypeException("Clientes do tipo Jurídica devem possuir uma conta do tipo Jurídica.");
        }
    }

    private void checkDuplicateAccountType(Client client, AccountType accountType) {
        if (accountRepository.existsByClientIdAndType(client.getId(), accountType)) {
            throw new DuplicateAccountTypeException("Cliente já possui uma conta do tipo " + accountType.name() + ".");
        }
    }
}