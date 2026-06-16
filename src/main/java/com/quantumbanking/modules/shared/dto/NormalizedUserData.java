package com.quantumbanking.modules.shared.dto;

public record NormalizedUserData(String name,
                                 String cpf,
                                 String phone,
                                 String email,
                                 String encryptedPassword,
                                 String street,
                                 String number,
                                 String complement,
                                 String neighborhood,
                                 String city,
                                 String state,
                                 String zipCode) {
}