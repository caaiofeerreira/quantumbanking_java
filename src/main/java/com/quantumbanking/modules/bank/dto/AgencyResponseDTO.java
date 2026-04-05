package com.quantumbanking.modules.bank.dto;

public record AgencyResponseDTO(Long id,
                                String agency_name,
                                String agency_number,
                                String city,
                                String state,
                                String zipCode) {
}
