package com.quantumbanking.modules.account.controller;

import com.quantumbanking.modules.account.dto.StatementResponseDTO;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.shared.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> balance(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getBalance(user.getId()));
    }

    @GetMapping("/statement")
    public ResponseEntity<StatementResponseDTO> statement(@AuthenticationPrincipal User user,
                                                          @RequestParam Integer month,
                                                          @RequestParam Integer year) {
        return ResponseEntity.ok(accountService.getStatement(user, month, year));

    }
}