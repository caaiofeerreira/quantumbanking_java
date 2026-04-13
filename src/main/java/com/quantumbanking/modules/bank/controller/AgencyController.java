package com.quantumbanking.modules.bank.controller;

import com.quantumbanking.modules.bank.dto.AgencyRegistrationDTO;
import com.quantumbanking.modules.bank.dto.AgencyResponseDTO;
import com.quantumbanking.modules.bank.service.AgencyService;
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
@RequestMapping("/api/admin")
public class AgencyController {

    private final AgencyService agencyService;

    @PostMapping("/agency/register")
    public ResponseEntity<AgencyResponseDTO> register(@RequestBody @Valid AgencyRegistrationDTO dto) {

        AgencyResponseDTO response = agencyService.createAgency(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}