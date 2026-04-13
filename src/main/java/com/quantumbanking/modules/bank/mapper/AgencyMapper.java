package com.quantumbanking.modules.bank.mapper;

import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.dto.AgencyResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class AgencyMapper {

    public AgencyResponseDTO toResponseDTO(Agency agency) {
        return new AgencyResponseDTO(
                agency.getId(),
                agency.getAgencyName(),
                agency.getAgencyNumber(),
                agency.getAddress().getCity(),
                agency.getAddress().getState(),
                agency.getAddress().getZipCode()
        );
    }
}