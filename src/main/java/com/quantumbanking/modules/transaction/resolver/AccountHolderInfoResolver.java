package com.quantumbanking.modules.transaction.resolver;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.client.repository.CompanyRepository;
import com.quantumbanking.modules.transaction.dto.AccountHolderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountHolderInfoResolver {

    private final CompanyRepository companyRepository;

    public AccountHolderInfo resolve(Account account) {

        Client client = account.getClient();

        if (client.getType() != ClientType.JURIDICA) {
            return new AccountHolderInfo(client.getName(), client.getCpf());
        }

        return companyRepository.findByClient(client)
                .map(company -> new AccountHolderInfo(company.getCompanyName(), company.getCnpj()))
                .orElseGet(() -> new AccountHolderInfo(client.getName(), client.getCpf()));
    }
}