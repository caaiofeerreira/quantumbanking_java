package com.quantumbanking.modules.manager.dto;

import com.quantumbanking.modules.bank.dto.AgencyResponseDTO;
import com.quantumbanking.modules.shared.domain.user.UserStatus;

public record ManagerResponseDTO(Long id,
                                 String name,
                                 String email,
                                 String phone,
                                 UserStatus status,
                                 AgencyResponseDTO agency) {
}
