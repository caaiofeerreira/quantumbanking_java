package com.quantumbanking.infra.security.auth;

import com.quantumbanking.infra.security.TokenService;
import com.quantumbanking.infra.security.dto.AuthRequestDTO;
import com.quantumbanking.infra.security.dto.DataToken;
import com.quantumbanking.modules.shared.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public DataToken authenticate(AuthRequestDTO requestDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.cpf(), requestDTO.password())
        );

        User user = (User) authentication.getPrincipal();
        String tokenJWT = tokenService.generateToken(user);

        return new DataToken(tokenJWT);
    }
}