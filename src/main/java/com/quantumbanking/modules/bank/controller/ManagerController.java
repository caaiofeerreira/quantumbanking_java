package com.quantumbanking.modules.bank.controller;

import com.quantumbanking.modules.bank.dto.AgencyAccountManagementDTO;
import com.quantumbanking.modules.bank.service.ManagerService;
import com.quantumbanking.modules.loan.dto.LoanApprovedResponseDTO;
import com.quantumbanking.modules.loan.dto.LoanManagerViewDTO;
import com.quantumbanking.modules.loan.dto.LoanResponseDTO;
import com.quantumbanking.modules.shared.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager/my-agency")
public class ManagerController {

    private final ManagerService managerService;

    @GetMapping("/accounts")
    public ResponseEntity<List<AgencyAccountManagementDTO>> getAgencyAccounts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(managerService.getAccountsByAgency(user.getId()));
    }

    @GetMapping("/loans/requested")
    public ResponseEntity<List<LoanManagerViewDTO>> getRequestedLoans(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(managerService.getLoanRequestsByAgency(user.getId()));
    }

    @PatchMapping("/loans/{loanId}/approve")
    public ResponseEntity<LoanApprovedResponseDTO> approveLoan(@AuthenticationPrincipal User user, @PathVariable UUID loanId) {
        return ResponseEntity.ok(managerService.approveLoan(user.getId(), loanId));
    }

    @PatchMapping("/loans/{loanId}/reject")
    public ResponseEntity<Void> rejectLoan(@AuthenticationPrincipal User user, @PathVariable UUID loanId) {
        managerService.rejectLoan(user.getId(), loanId);
        return ResponseEntity.noContent().build();
    }
}