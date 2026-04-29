package com.quantumbanking.modules.bank.controller;

import com.quantumbanking.modules.bank.dto.AgencyAccountManagementDTO;
import com.quantumbanking.modules.bank.service.ManagerService;
import com.quantumbanking.modules.shared.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager")
public class ManagerController {

    private final ManagerService managerService;

    @GetMapping("/accounts")
    public ResponseEntity<List<AgencyAccountManagementDTO>> list(@AuthenticationPrincipal User user) {

        List<AgencyAccountManagementDTO> accounts = managerService.listAgencyAccounts(user);
        return ResponseEntity.ok(accounts);
    }
}