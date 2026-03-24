package com.quantumbanking.modules.client.controller;

import com.quantumbanking.modules.client.dto.ClientRegistrationDTO;
import com.quantumbanking.modules.client.dto.ClientResponseDTO;
import com.quantumbanking.modules.client.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/client")
public class ClientController {

    private final ClientService clientService;

    @PostMapping("/register")
    public ResponseEntity<ClientResponseDTO> register(@RequestBody @Valid ClientRegistrationDTO dto) {
        ClientResponseDTO response = clientService.clientRegister(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}