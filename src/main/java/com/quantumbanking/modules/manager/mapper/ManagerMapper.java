package com.quantumbanking.modules.manager.mapper;


import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.mapper.AgencyMapper;
import com.quantumbanking.modules.manager.domain.Manager;
import com.quantumbanking.modules.manager.dto.ManagerBasicViewDTO;
import com.quantumbanking.modules.manager.dto.ManagerProfileResponseDTO;
import com.quantumbanking.modules.manager.dto.ManagerResponseDTO;
import com.quantumbanking.modules.shared.mapper.AddressMapper;
import com.quantumbanking.modules.shared.util.DataMaskingUtils;
import com.quantumbanking.modules.shared.util.FormattingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManagerMapper {

    private final AgencyMapper agencyMapper;
    private final AddressMapper addressMapper;

    public ManagerResponseDTO toManagerResponseDTO(Manager manager, Agency agency) {
        return new ManagerResponseDTO(
                manager.getId(),
                manager.getName(),
                manager.getEmail(),
                FormattingUtils.formatPhone(manager.getPhone()),
                manager.getStatus(),
                agencyMapper.toAgencyResponseDTO(agency)
        );
    }

    public ManagerBasicViewDTO toManagerBasicViewDTO(Manager manager) {
        return new ManagerBasicViewDTO(
                manager.getId(),
                manager.getName(),
                manager.getEmail(),
                FormattingUtils.formatPhone(manager.getPhone()),
                manager.getStatus()
        );
    }

    public ManagerProfileResponseDTO toProfileResponseDTO(Manager manager) {
        return new ManagerProfileResponseDTO(
                manager.getName(),
                manager.getEmail(),
                FormattingUtils.formatPhone(manager.getPhone()),
                addressMapper.toAddressDTO(manager.getAddress()),
                manager.getStatus()
        );
    }
}