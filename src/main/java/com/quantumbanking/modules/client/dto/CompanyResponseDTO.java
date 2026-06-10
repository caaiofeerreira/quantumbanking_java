package com.quantumbanking.modules.client.dto;

import com.quantumbanking.modules.shared.dto.AddressDTO;

public record CompanyResponseDTO(String companyName,
                                 String tradeName,
                                 String cnpj,
                                 String stateRegistration,
                                 AddressDTO address) {

}