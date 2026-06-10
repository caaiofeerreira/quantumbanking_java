package com.quantumbanking.modules.bank.mapper;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.dto.AgencyAccountManagementDTO;
import com.quantumbanking.modules.bank.dto.AgencyResponseDTO;
import com.quantumbanking.modules.shared.mapper.AddressMapper;
import com.quantumbanking.modules.shared.util.DataMaskingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgencyMapper {

    private final AddressMapper addressMapper;

    public AgencyResponseDTO toAgencyResponseDTO(Agency agency) {
        return new AgencyResponseDTO(
                agency.getId(),
                agency.getAgencyName(),
                agency.getAgencyNumber(),
                agency.getPhone(),
                addressMapper.toAddressDTO(agency.getAddress()),
                agency.getBank().getName()
        );
    }

    public AgencyAccountManagementDTO toAccountManagementDTO(Account account) {
        return new AgencyAccountManagementDTO(
                account.getClient().getName(),
                DataMaskingUtils.maskCpf(account.getClient().getCpf()),
                account.getClient().getEmail(),
                account.getClient().getPhone(),
                account.getClient().getType(),
                account.getId(),
                account.getAccountNumber(),
                account.getType(),
                account.getBalance(),
                account.getStatus(),
                account.getAgency().getAgencyNumber());
    }
}