package com.quantumbanking.modules.account.mapper;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.dto.AccountResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    public AccountResponseDTO toResponseDTO(Account account) {
        return new AccountResponseDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getType(),
                account.getBalance(),
                account.getStatus(),
                account.getAgency().getAgencyNumber()
        );
    }
}