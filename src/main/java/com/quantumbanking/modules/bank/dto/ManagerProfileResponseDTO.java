package com.quantumbanking.modules.bank.dto;

import com.quantumbanking.modules.shared.domain.user.UserStatus;
import com.quantumbanking.modules.shared.dto.AddressDTO;

public record ManagerProfileResponseDTO(String name,
                                        String cpf,
                                        String email,
                                        String phone,
                                        AddressDTO address,
                                        UserStatus status) {
}
