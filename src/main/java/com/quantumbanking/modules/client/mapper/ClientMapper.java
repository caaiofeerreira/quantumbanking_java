package com.quantumbanking.modules.client.mapper;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.mapper.AccountMapper;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.Company;
import com.quantumbanking.modules.client.dto.ClientResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientMapper {

    private final AccountMapper accountMapper;
    private final CompanyMapper companyMapper;

    public ClientResponseDTO toClientResponseDTO(Client client, Account account, Company company) {
        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                client.getCpf(),
                client.getEmail(),
                client.getPhone(),
                client.getType(),
                client.getStatus(),
                accountMapper.toAccountResponseDTO(account),
                company != null ? companyMapper.toCompanyResponseDTO(company) : null
        );
    }
}