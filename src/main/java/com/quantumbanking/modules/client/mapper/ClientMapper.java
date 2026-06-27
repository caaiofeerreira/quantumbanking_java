package com.quantumbanking.modules.client.mapper;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.mapper.AccountMapper;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.Company;
import com.quantumbanking.modules.client.dto.ClientProfileResponseDTO;
import com.quantumbanking.modules.client.dto.ClientResponseDTO;
import com.quantumbanking.modules.client.dto.CompanyResponseDTO;
import com.quantumbanking.modules.shared.mapper.AddressMapper;
import com.quantumbanking.modules.shared.util.DataMaskingUtils;
import com.quantumbanking.modules.shared.util.FormattingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientMapper {

    private final AccountMapper accountMapper;
    private final CompanyMapper companyMapper;
    private final AddressMapper addressMapper;

    public ClientResponseDTO toClientResponseDTO(Client client, Account account, Company company) {
        return new ClientResponseDTO(
                client.getName(),
                DataMaskingUtils.maskCpf(client.getCpf()),
                client.getEmail(),
                FormattingUtils.formatPhone(client.getPhone()),
                client.getType(),
                client.getStatus(),
                accountMapper.toAccountResponseDTO(account),
                mapCompany(company)
        );
    }

    public ClientProfileResponseDTO toProfileResponseDTO(Client client, Company company) {

        return new ClientProfileResponseDTO(
                client.getName(),
                client.getEmail(),
                FormattingUtils.formatPhone(client.getPhone()),
                addressMapper.toAddressDTO(client.getAddress()),
                client.getType(),
                client.getStatus(),
                mapCompany(company)
        );
    }

    private CompanyResponseDTO mapCompany(Company company) {
        if (company != null) {
            return companyMapper.toCompanyResponseDTO(company);
        }
        return null;
    }
}