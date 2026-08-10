package com.quantumbanking.modules.account.controller;

import com.quantumbanking.modules.account.domain.AccountType;
import com.quantumbanking.modules.account.dto.AccountResponseDTO;
import com.quantumbanking.modules.account.dto.AccountSummaryDTO;
import com.quantumbanking.modules.account.dto.MultiMonthStatementResponseDTO;
import com.quantumbanking.modules.account.dto.StatementResponseDTO;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.shared.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> balance(@AuthenticationPrincipal User user,
                                              @PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getBalance(user.getId(), accountNumber));
    }

    @GetMapping("/{accountNumber}/statement")
    public ResponseEntity<MultiMonthStatementResponseDTO> statement(@AuthenticationPrincipal User user,
                                                                    @PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.buildLastThreeMonthsStatement(user.getId(), accountNumber));
    }

    @GetMapping("/{accountNumber}/statement/period")
    public ResponseEntity<StatementResponseDTO> statementByPeriod(@AuthenticationPrincipal User user,
                                                                  @PathVariable String accountNumber,
                                                                  @RequestParam Integer month,
                                                                  @RequestParam Integer year) {
        return ResponseEntity.ok(accountService.getStatement(user.getId(), accountNumber, month, year));
    }

    @GetMapping("/my-accounts")
    public ResponseEntity<List<AccountSummaryDTO>> myAccounts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getMyAccounts(user.getId()));
    }

    @PostMapping("/create/corrente")
    public ResponseEntity<AccountResponseDTO> createCorrente(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(201).body(accountService.openComplementaryAccount(user.getId(), AccountType.CORRENTE));
    }

    @PostMapping("/create/poupanca")
    public ResponseEntity<AccountResponseDTO> createPoupanca(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(201).body(accountService.openComplementaryAccount(user.getId(), AccountType.POUPANCA));
    }
}