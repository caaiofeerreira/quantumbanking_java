package com.quantumbanking.infra.security.auth;

import com.quantumbanking.infra.security.dto.AuthRequestDTO;
import com.quantumbanking.infra.security.dto.DataToken;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<DataToken> login(@RequestBody @Valid AuthRequestDTO auth) {

        DataToken dataToken = authService.authenticate(auth);
        return ResponseEntity.ok(dataToken);
    }
}