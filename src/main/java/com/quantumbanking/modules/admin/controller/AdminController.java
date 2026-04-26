package com.quantumbanking.modules.admin.controller;

import com.quantumbanking.modules.admin.service.AdminService;
import com.quantumbanking.modules.bank.dto.AgencyRegistrationDTO;
import com.quantumbanking.modules.bank.dto.AgencyResponseDTO;
import com.quantumbanking.modules.bank.dto.ManagerRegistrationDTO;
import com.quantumbanking.modules.bank.dto.ManagerResponseDTO;
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
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/agency/register")
    public ResponseEntity<AgencyResponseDTO> registerAgency(@RequestBody @Valid AgencyRegistrationDTO dto) {

        AgencyResponseDTO response = adminService.registerAgency(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/manager/register")
    public ResponseEntity<ManagerResponseDTO> registerManager(@RequestBody @Valid ManagerRegistrationDTO dto) {

        ManagerResponseDTO response = adminService.registerManager(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}