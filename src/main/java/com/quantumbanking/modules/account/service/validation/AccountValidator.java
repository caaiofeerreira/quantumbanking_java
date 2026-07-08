package com.quantumbanking.modules.account.service.validation;

import com.quantumbanking.infra.exception.DuplicateAccountTypeException;
import com.quantumbanking.infra.exception.IncompatibleAccountTypeException;
import com.quantumbanking.modules.account.domain.AccountType;
import com.quantumbanking.modules.account.repository.AccountRepository;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.ClientType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountValidator {

    private final AccountRepository accountRepository;

    public void validateAccount(ClientType clientType, AccountType accountType, Client client) {
        checkCompatibleAccountType(clientType, accountType);
        checkDuplicateAccountType(client, accountType);
    }

    private void checkCompatibleAccountType(ClientType clientType, AccountType accountType) {
        if (clientType == ClientType.FISICA && accountType == AccountType.JURIDICA) {
            throw new IncompatibleAccountTypeException("Pessoa física não pode ter conta jurídica.");
        }
    }

    private void checkDuplicateAccountType(Client client, AccountType accountType) {
        if (accountRepository.existsByClientIdAndType(client.getId(), accountType)) {
            throw new DuplicateAccountTypeException("Cliente já possui uma conta do tipo " + accountType.name() + ".");
        }
    }
}