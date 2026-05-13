package com.quantumbanking.modules.shared.service;

import com.quantumbanking.infra.exception.CpfAlreadyRegisteredException;
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

        return userRepository.findByCpf(cpf)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
    }

    public void validateCpfNotRegistered(String cpf) {
        if (userRepository.existsByCpf(cpf)) {
            throw new CpfAlreadyRegisteredException("Este CPF já está vinculado a outro usuário.");
        }
    }
}