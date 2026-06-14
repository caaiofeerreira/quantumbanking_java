package com.quantumbanking.modules.transaction.controller;

import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.transaction.dto.*;
import com.quantumbanking.modules.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponseDTO> deposit(@AuthenticationPrincipal User user,
                                                      @RequestBody @Valid DepositRequestDTO requestDTO) {
        return ResponseEntity.ok(transactionService.executeDeposit(user.getId(), requestDTO));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponseDTO> withdraw(@AuthenticationPrincipal User user,
                                                        @RequestBody @Valid WithdrawRequestDTO requestDTO) {
        return ResponseEntity.ok(transactionService.executeWithdraw(user.getId(), requestDTO));
    }

    @PostMapping("/internal")
    public ResponseEntity<InternalTransactionResponseDTO> internalTransaction(@AuthenticationPrincipal User user,
                                                                              @RequestBody @Valid InternalTransactionRequestDTO requestDTO) {
        return ResponseEntity.ok(transactionService.executeInternalTransaction(user, requestDTO));
    }

    @PostMapping("/external")
    public ResponseEntity<ExternalTransactionResponseDTO> externalTransaction(@AuthenticationPrincipal User user,
                                                                              @RequestBody @Valid ExternalTransactionRequestDTO requestDTO) {
        return ResponseEntity.ok(transactionService.executeExternalTransaction(user, requestDTO));
    }

    @PostMapping("/pix")
    public ResponseEntity<PixTransactionResponseDTO> pix(@AuthenticationPrincipal User user,
                                                         @RequestBody @Valid PixTransactionRequestDTO requestDTO) {
        return ResponseEntity.ok(transactionService.executePixTransaction(user, requestDTO));
    }
}