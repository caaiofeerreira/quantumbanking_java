package com.quantumbanking.modules.client.dto;

import com.quantumbanking.modules.account.dto.AccountResponseDTO;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.shared.domain.user.UserStatus;

public record ClientResponseDTO(
        Long clientId,
        String name,
        String cpf,
        String email,
        String phone,
        ClientType clientType,
        UserStatus status,
        AccountResponseDTO account,
        CompanyResponseDTO company) {

}