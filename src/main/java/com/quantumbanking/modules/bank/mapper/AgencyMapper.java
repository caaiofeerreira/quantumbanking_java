package com.quantumbanking.modules.bank.mapper;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.mapper.AccountMapper;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.dto.AgencyAccountManagementDTO;
import com.quantumbanking.modules.bank.dto.AgencyResponseDTO;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.shared.mapper.AddressMapper;
import com.quantumbanking.modules.shared.util.FormattingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AgencyMapper {

    private final AddressMapper addressMapper;
    private final AccountMapper accountMapper;

    public AgencyResponseDTO toAgencyResponseDTO(Agency agency) {
        return new AgencyResponseDTO(
                agency.getId(),
                agency.getAgencyName(),
                agency.getAgencyNumber(),
                FormattingUtils.formatPhone(agency.getPhone()),
                addressMapper.toAddressDTO(agency.getAddress()),
                agency.getBank().getName()
        );
    }

    public AgencyAccountManagementDTO toAccountManagementDTO(Client client, List<Account> accounts) {
        return new AgencyAccountManagementDTO(
                client.getName(),
                client.getEmail(),
                FormattingUtils.formatPhone(client.getPhone()),
                client.getType(),
                accounts.stream()
                        .map(accountMapper::toAccountSummaryDTO)
                        .toList());
    }
}