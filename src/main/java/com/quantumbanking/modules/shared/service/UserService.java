package com.quantumbanking.modules.shared.service;

import com.quantumbanking.infra.exception.UserNotFoundException;
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

    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {

        String normalizedCpf = cpf.replaceAll("[^0-9]", "");

        return userRepository.findByCpf(normalizedCpf)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
    }
}