package com.quantumbanking.modules.client.controller;

import com.quantumbanking.modules.client.dto.JuridicaRegistrationDTO;
import com.quantumbanking.modules.client.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/juridica")
    public ResponseEntity<Void> registerJuridica(@RequestBody @Valid JuridicaRegistrationDTO dto) {
        companyService.registerJuridica(dto);
        return ResponseEntity.status(201).build();
    }
}
