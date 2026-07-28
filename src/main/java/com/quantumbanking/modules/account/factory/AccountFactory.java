package com.quantumbanking.modules.account.factory;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.AccountStatus;
import com.quantumbanking.modules.account.domain.AccountType;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.client.domain.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AccountFactory {

    public Account createDefaultAccount(String accountNumber,AccountType accountType, Agency agency, Client client) {

        return Account.builder()
                .accountNumber(accountNumber)
                .agency(agency)
                .client(client)
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .type(accountType)
                .build();
    }
}