package com.quantumbanking.modules.client.dto;

import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.shared.domain.user.UserStatus;
import com.quantumbanking.modules.shared.dto.AddressDTO;

public record ClientProfileResponseDTO(String name,
                                       String cpf,
                                       String email,
                                       String phone,
                                       AddressDTO address,
                                       ClientType clientType,
                                       UserStatus status,
                                       CompanyResponseDTO company) {
}