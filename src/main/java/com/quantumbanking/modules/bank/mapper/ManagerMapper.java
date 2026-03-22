package com.quantumbanking.modules.bank.mapper;

import com.quantumbanking.modules.bank.domain.manager.Manager;
import com.quantumbanking.modules.bank.dto.ManagerResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManagerMapper {

    private final AgencyMapper agencyMapper;

    public ManagerResponseDTO toRegister(Manager manager) {
        return new ManagerResponseDTO(
                manager.getId(),
                manager.getName(),
                manager.getCpf(),
                manager.getEmail(),
                manager.getPhone(),
                manager.getStatus(),
                agencyMapper.toAgency(manager.getAgency())
        );
    }
}
