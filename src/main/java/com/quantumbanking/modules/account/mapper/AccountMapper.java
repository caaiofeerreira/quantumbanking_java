package com.quantumbanking.modules.account.mapper;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.dto.AccountResponseDTO;
import com.quantumbanking.modules.shared.util.FormattingUtils;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponseDTO toAccountResponseDTO(Account account) {
        return new AccountResponseDTO(
                account.getId(),
                FormattingUtils.formatAccountNumber(account.getAccountNumber()),
                account.getType(),
                account.getBalance(),
                account.getStatus(),
                account.getAgency().getAgencyNumber()
        );
    }
}