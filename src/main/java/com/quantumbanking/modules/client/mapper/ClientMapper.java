package com.quantumbanking.modules.client.mapper;

import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.dto.ClientProfileResponseDTO;
import com.quantumbanking.modules.shared.mapper.AddressMapper;
import com.quantumbanking.modules.shared.util.FormattingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientMapper {

    private final AddressMapper addressMapper;

    public ClientProfileResponseDTO toProfileResponseDTO(Client client) {

        return new ClientProfileResponseDTO(
                client.getName(),
                client.getEmail(),
                FormattingUtils.formatPhone(client.getPhone()),
                addressMapper.toAddressDTO(client.getAddress()),
                client.getType(),
                client.getStatus()
        );
    }
}