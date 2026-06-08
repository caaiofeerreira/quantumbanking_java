package com.quantumbanking.modules.bank.dto;

import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.domain.user.UserStatus;

public record ManagerProfileResponseDTO(String name,
                                        String cpf,
                                        String email,
                                        String phone,
                                        Address address,
                                        UserStatus status) {
}
