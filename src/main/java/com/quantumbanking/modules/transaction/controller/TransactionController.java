package com.quantumbanking.modules.transaction.controller;

import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.transaction.dto.DepositRequestDTO;
import com.quantumbanking.modules.transaction.dto.DepositResponseDTO;
import com.quantumbanking.modules.transaction.dto.WithdrawRequestDTO;
import com.quantumbanking.modules.transaction.dto.WithdrawResponseDTO;
import com.quantumbanking.modules.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/account")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponseDTO> deposit(@AuthenticationPrincipal User user,
                                                      @RequestBody @Valid DepositRequestDTO requestDTO) {

        DepositResponseDTO responseDTO = transactionService.executeDeposit(user, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponseDTO> withdraw(@AuthenticationPrincipal User user,
                                                        @RequestBody @Valid WithdrawRequestDTO requestDTO) {

        WithdrawResponseDTO responseDTO = transactionService.executeWithdraw(user, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}