package com.quantumbanking.modules.account.service;

import com.quantumbanking.infra.exception.AccountNotFoundException;
import com.quantumbanking.modules.account.repository.AccountRepository;
import com.quantumbanking.modules.shared.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public BigDecimal getBalance(User user) {

        return accountRepository.findBalanceByClientId(user.getId())
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada."));
    }
}
