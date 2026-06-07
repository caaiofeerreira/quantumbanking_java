package com.quantumbanking.modules.client.dto;

import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.domain.user.UserStatus;

public record ClientProfileResponseDTO(String name,
                                       String cpf,
                                       String email,
                                       String phone,
                                       Address address,
                                       ClientType clientType,
                                       UserStatus status) {
}