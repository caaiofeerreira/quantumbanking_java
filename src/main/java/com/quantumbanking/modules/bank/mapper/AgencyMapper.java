package com.quantumbanking.modules.bank.mapper;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.dto.AgencyAccountManagementDTO;
import com.quantumbanking.modules.bank.dto.AgencyResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class AgencyMapper {

    public AgencyResponseDTO toAgencyResponseDTO(Agency agency) {
        return new AgencyResponseDTO(
                agency.getId(),
                agency.getAgencyName(),
                agency.getAgencyNumber(),
                agency.getAddress().getCity(),
                agency.getAddress().getState(),
                agency.getAddress().getZipCode()
        );
    }

    public AgencyAccountManagementDTO toAccountManagementDTO(Account account) {
        return new AgencyAccountManagementDTO(
                account.getClient().getName(),
                account.getClient().getCpf(),
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