package com.quantumbanking.modules.account.factory;

import com.quantumbanking.infra.exception.ValidateException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.AccountStatus;
import com.quantumbanking.modules.account.domain.AccountType;
import com.quantumbanking.modules.account.repository.AccountRepository;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.client.domain.ClientType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class AccountFactory {

    private final AccountRepository accountRepository;

    public Account createDefaultAccount(ClientType clientType, AccountType accountType, Agency agency) {
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setAgency(agency);
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);

        if (clientType == ClientType.FISICA && accountType == AccountType.JURIDICA) {
            throw new ValidateException("Pessoa física não pode ter conta jurídica.");
        }
        account.setType(accountType);

        return account;
    }

    private String generateAccountNumber() {
        String number;
        String fullNumber;
        do {
            number = String.format("%08d", new Random().nextInt(99999999));
            fullNumber = number + "-" + generateVerifierDigit(number);
        } while (accountRepository.existsByAccountNumber(fullNumber));
        return fullNumber;
    }

    private String generateVerifierDigit(String accountNumber) {
        int sum = 0;
        int multiplier = 2;
        for (int i = accountNumber.length() - 1; i >= 0; i--) {
            sum += Character.getNumericValue(accountNumber.charAt(i)) * multiplier;
            multiplier = multiplier == 9 ? 2 : multiplier + 1;
        }
        int remainder = sum % 11;
        if (remainder == 0 || remainder == 1) return "0";
        return String.valueOf(11 - remainder);
    }
}