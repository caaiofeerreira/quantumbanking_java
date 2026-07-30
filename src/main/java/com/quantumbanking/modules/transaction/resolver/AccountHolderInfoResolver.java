package com.quantumbanking.modules.transaction.resolver;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.AccountType;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.repository.CompanyRepository;
import com.quantumbanking.modules.shared.util.DataMaskingUtils;
import com.quantumbanking.modules.transaction.dto.AccountHolderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountHolderInfoResolver {

    private final CompanyRepository companyRepository;

    public AccountHolderInfo resolve(Account account) {

        Client client = account.getClient();

        if (account.getType() != AccountType.JURIDICA) {
            return new AccountHolderInfo(client.getName(), DataMaskingUtils.maskCpf(client.getCpf()));
        }

        return companyRepository.findByClient(client)
                .map(company -> new AccountHolderInfo(company.getTradeName(), company.getCnpj()))
                .orElseGet(() -> new AccountHolderInfo(client.getName(), DataMaskingUtils.maskCpf(client.getCpf())));
    }
}