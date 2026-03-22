package com.quantumbanking.modules.bank.dto;

import com.quantumbanking.modules.shared.domain.user.UserStatus;

public record ManagerResponseDTO(Long id,
                                 String name,
                                 String cpf,
                                 String email,
                                 String phone,
                                 UserStatus status,
                                 AgencyResponseDTO agency) {
}
