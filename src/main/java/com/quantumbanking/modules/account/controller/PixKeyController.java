package com.quantumbanking.modules.account.controller;

import com.quantumbanking.modules.account.dto.PixKeyRequestDTO;
import com.quantumbanking.modules.account.dto.PixKeyResponseDTO;
import com.quantumbanking.modules.account.service.PixKeyService;
import com.quantumbanking.modules.shared.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account/{accountNumber}/pix/keys")
public class PixKeyController {

    private final PixKeyService pixKeyService;

    @PostMapping()
    public ResponseEntity<PixKeyResponseDTO> register(@AuthenticationPrincipal User user,
                                                      @PathVariable String accountNumber,
                                                      @RequestBody @Valid PixKeyRequestDTO requestDTO) {
        return ResponseEntity.status(201).body(pixKeyService.registerPixKey(user.getId(), accountNumber, requestDTO));
    }

    @GetMapping()
    public ResponseEntity<List<PixKeyResponseDTO>> list(@AuthenticationPrincipal User user,
                                                        @PathVariable String accountNumber) {
        return ResponseEntity.ok(pixKeyService.listPixKey(user.getId(), accountNumber));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal User user,
                                       @PathVariable String key) {
        pixKeyService.removePixKey(user.getId(), key);
        return ResponseEntity.noContent().build();
    }
}