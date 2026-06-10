package com.quantumbanking.modules.shared.dto;

public record AddressDTO(String street,
                         String number,
                         String complement,
                         String neighborhood,
                         String city,
                         String state,
                         String zipCode) {
}