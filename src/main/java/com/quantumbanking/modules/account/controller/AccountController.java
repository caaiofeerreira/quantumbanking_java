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
import java.text.NumberFormat;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/balance")
    public ResponseEntity<String> balance(@AuthenticationPrincipal User user) {

        BigDecimal balance = accountService.getBalance(user);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String formatBalance = numberFormat.format(balance);

        return ResponseEntity.ok(String.format("Saldo: %s", formatBalance));
    }

    @GetMapping("/statement")
    public ResponseEntity<StatementResponseDTO> statement(@AuthenticationPrincipal User user,
                                                          @RequestParam Integer month,
                                                          @RequestParam Integer year) {

        StatementResponseDTO responseDTO = accountService.getStatement(user, month, year);
        return ResponseEntity.ok(responseDTO);

    }
}
