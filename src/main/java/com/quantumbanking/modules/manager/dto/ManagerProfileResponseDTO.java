package com.quantumbanking.modules.manager.dto;

import com.quantumbanking.modules.shared.domain.user.UserStatus;
import com.quantumbanking.modules.shared.dto.AddressDTO;

public record ManagerProfileResponseDTO(String name,
                                        String email,
                                        String phone,
                                        AddressDTO address,
                                        UserStatus status) {
}
