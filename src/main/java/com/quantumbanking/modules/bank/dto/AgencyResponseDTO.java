package com.quantumbanking.modules.bank.dto;

import com.quantumbanking.modules.shared.dto.AddressDTO;

public record AgencyResponseDTO(Long id,
                                String agencyName,
                                String agencyNumber,
                                String phone,
                                AddressDTO address,
                                String bankName) {
}
