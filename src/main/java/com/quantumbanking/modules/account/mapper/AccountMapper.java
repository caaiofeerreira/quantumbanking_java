package com.quantumbanking.modules.account.mapper;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.dto.AccountResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponseDTO toAccountResponseDTO(Account account) {
        return new AccountResponseDTO(
                account.getId(),
                formatAccountNumber(account.getAccountNumber()),
                account.getType(),
                account.getBalance(),
                account.getStatus(),
                account.getAgency().getAgencyNumber()
        );
    }

    private String formatAccountNumber(String number) {
        if (number == null || number.length() < 2) {
            return number;
        }
        int splitIndex = number.length() - 1;
        return number.substring(0, splitIndex) + "-" + number.substring(splitIndex);
    }
}