package com.quantumbanking.modules.admin.controller;

import com.quantumbanking.modules.admin.service.AdminService;
import com.quantumbanking.modules.bank.dto.AgencyRegistrationDTO;
import com.quantumbanking.modules.bank.dto.AgencyResponseDTO;
import com.quantumbanking.modules.bank.dto.ManagerRegistrationDTO;
import com.quantumbanking.modules.bank.dto.ManagerResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/agency")
    public ResponseEntity<AgencyResponseDTO> registerAgency(@RequestBody @Valid AgencyRegistrationDTO dto) {
        return ResponseEntity.status(201).body(adminService.registerAgency(dto));
    }

    @GetMapping("/agencies")
    public ResponseEntity<List<AgencyResponseDTO>> getAllAgencies() {
        return ResponseEntity.ok(adminService.getAllAgencies());
    }

    @PostMapping("/manager")
    public ResponseEntity<ManagerResponseDTO> registerManager(@RequestBody @Valid ManagerRegistrationDTO dto) {
        return ResponseEntity.status(201).body(adminService.registerManager(dto));
    }

    @GetMapping("/managers")
    public ResponseEntity<List<ManagerResponseDTO>> getAllManagers() {
        return ResponseEntity.ok(adminService.getAllManagers());
    }
}