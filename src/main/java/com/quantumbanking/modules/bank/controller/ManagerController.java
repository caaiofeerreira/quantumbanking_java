package com.quantumbanking.modules.bank.controller;

import com.quantumbanking.modules.bank.dto.ManagerRegistrationDTO;
import com.quantumbanking.modules.bank.dto.ManagerResponseDTO;
import com.quantumbanking.modules.bank.service.ManagerService;
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
@RequestMapping("/api/auth/manager")
public class ManagerController {

    private final ManagerService managerService;

    @PostMapping("/register")
    public ResponseEntity<ManagerResponseDTO> register(@RequestBody @Valid ManagerRegistrationDTO dto) {

        ManagerResponseDTO response = managerService.managerRegister(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
