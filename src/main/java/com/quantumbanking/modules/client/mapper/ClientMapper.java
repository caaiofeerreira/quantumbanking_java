package com.quantumbanking.modules.client.mapper;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.mapper.AccountMapper;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.Company;
import com.quantumbanking.modules.client.dto.ClientProfileResponseDTO;
import com.quantumbanking.modules.client.dto.ClientResponseDTO;
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
                client.getId(),
                client.getName(),
                DataMaskingUtils.maskCpf(client.getCpf()),
                client.getEmail(),
                FormattingUtils.formatPhone(client.getPhone()),
                client.getType(),
                client.getStatus(),
                accountMapper.toAccountResponseDTO(account),
                company != null ? companyMapper.toCompanyResponseDTO(company) : null
        );
    }

    public ClientProfileResponseDTO toProfileResponseDTO(Client client) {

        return new ClientProfileResponseDTO(
                client.getName(),
                DataMaskingUtils.maskCpf(client.getCpf()),
                client.getEmail(),
                FormattingUtils.formatPhone(client.getPhone()),
                addressMapper.toAddressDTO(client.getAddress()),
                client.getType(),
                client.getStatus()
        );
    }
}