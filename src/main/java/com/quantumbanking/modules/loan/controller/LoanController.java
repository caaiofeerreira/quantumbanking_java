package com.quantumbanking.modules.loan.controller;

import com.quantumbanking.modules.loan.dto.LoanRequestDTO;
import com.quantumbanking.modules.loan.dto.LoanResponseDTO;
import com.quantumbanking.modules.loan.service.LoanService;
import com.quantumbanking.modules.shared.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account/loan")
public class LoanController {

    private final LoanService loanService;

    @PostMapping()
    public ResponseEntity<LoanResponseDTO> loan(@AuthenticationPrincipal User user,
                                                @RequestParam String accountNumber,
                                                @RequestBody @Valid LoanRequestDTO requestDTO) {
        return ResponseEntity.status(201).body(loanService.processLoan(user.getId(), accountNumber, requestDTO));
    }
}
