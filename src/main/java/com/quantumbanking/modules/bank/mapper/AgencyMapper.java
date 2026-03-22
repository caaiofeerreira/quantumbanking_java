package com.quantumbanking.modules.bank.mapper;

import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.dto.AgencyResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class AgencyMapper {

    public AgencyResponseDTO toAgency(Agency agency) {
        return new AgencyResponseDTO(
                agency.getId(),
                agency.getNumber(),
                agency.getNumber()
        );
    }
}