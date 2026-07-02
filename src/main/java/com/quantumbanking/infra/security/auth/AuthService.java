package com.quantumbanking.infra.security.auth;

import com.quantumbanking.infra.exception.InvalidCredentialsException;
import com.quantumbanking.infra.security.TokenRedisService;
import com.quantumbanking.infra.security.TokenService;
import com.quantumbanking.infra.security.dto.AuthRequestDTO;
import com.quantumbanking.infra.security.dto.DataToken;
import com.quantumbanking.modules.shared.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;
    private final TokenRedisService tokenRedisService;

    public DataToken authenticate(AuthRequestDTO requestDTO) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDTO.cpf(), requestDTO.password())
            );

            User user = (User) authentication.getPrincipal();
            String tokenJWT = tokenService.generateToken(user);
            tokenRedisService.saveActiveToken(user.getId(), tokenService.getClaims(tokenJWT).jti()); // alterado

            return new DataToken(tokenJWT);

        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            throw new InvalidCredentialsException("CPF ou senha inválidos.");
        }
    }
}