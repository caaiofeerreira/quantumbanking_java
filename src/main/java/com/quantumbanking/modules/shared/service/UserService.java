package com.quantumbanking.modules.shared.service;

import com.quantumbanking.infra.exception.UserNotFoundException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.repository.AccountRepository;
import com.quantumbanking.modules.bank.domain.manager.Manager;
import com.quantumbanking.modules.bank.repository.ManagerRepository;
import com.quantumbanking.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final ManagerRepository managerRepository;

    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {

        return userRepository.findByCpf(cpf)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
    }

    public Account getAuthenticatedUserAccount(Long userId) {

        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Conta não encontrada."));
    }

    public Manager getAuthenticatedUserManager(Long userId) {

        return managerRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Gerente não encontrado."));
    }
}