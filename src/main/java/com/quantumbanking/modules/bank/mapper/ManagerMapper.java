package com.quantumbanking.modules.bank.mapper;

import com.quantumbanking.modules.bank.domain.manager.Manager;
import com.quantumbanking.modules.bank.dto.ManagerResponseDTO;
import com.quantumbanking.modules.shared.util.DataMaskingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManagerMapper {

    private final AgencyMapper agencyMapper;

    public ManagerResponseDTO toManagerResponseDTO(Manager manager) {
        return new ManagerResponseDTO(
                manager.getId(),
                manager.getName(),
                DataMaskingUtils.maskCpf(manager.getCpf()),
                manager.getEmail(),
                manager.getPhone(),
                manager.getStatus(),
                agencyMapper.toAgencyResponseDTO(manager.getAgency())
        );
    }
}
