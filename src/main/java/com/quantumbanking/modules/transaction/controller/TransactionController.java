package com.quantumbanking.modules.transaction.controller;

import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.transaction.dto.*;
import com.quantumbanking.modules.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account/{accountNumber}/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponseDTO> deposit(@AuthenticationPrincipal User user,
                                                      @PathVariable String accountNumber,
                                                      @RequestBody @Valid DepositRequestDTO requestDTO) {
        return ResponseEntity.ok(transactionService.executeDeposit(user.getId(), accountNumber, requestDTO));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponseDTO> withdraw(@AuthenticationPrincipal User user,
                                                        @PathVariable String accountNumber,
                                                        @RequestBody @Valid WithdrawRequestDTO requestDTO) {
        return ResponseEntity.ok(transactionService.executeWithdraw(user.getId(), accountNumber, requestDTO));
    }

    @PostMapping("/internal")
    public ResponseEntity<InternalTransactionResponseDTO> internalTransaction(@AuthenticationPrincipal User user,
                                                                              @PathVariable String accountNumber,
                                                                              @RequestBody @Valid InternalTransactionRequestDTO requestDTO) {
        return ResponseEntity.ok(transactionService.executeInternalTransaction(user, accountNumber, requestDTO));
    }

    @PostMapping("/external")
    public ResponseEntity<ExternalTransactionResponseDTO> externalTransaction(@AuthenticationPrincipal User user,
                                                                              @PathVariable String accountNumber,
                                                                              @RequestBody @Valid ExternalTransactionRequestDTO requestDTO) {
        return ResponseEntity.ok(transactionService.executeExternalTransaction(user, accountNumber, requestDTO));
    }

    @PostMapping("/pix")
    public ResponseEntity<PixTransactionResponseDTO> pix(@AuthenticationPrincipal User user,
                                                         @PathVariable String accountNumber,
                                                         @RequestBody @Valid PixTransactionRequestDTO requestDTO) {
        return ResponseEntity.ok(transactionService.executePixTransaction(user, accountNumber, requestDTO));
    }
}