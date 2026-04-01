package com.quantumbanking.modules.account.controller;

import com.quantumbanking.modules.account.dto.PixKeyRequestDTO;
import com.quantumbanking.modules.account.dto.PixKeyResponseDTO;
import com.quantumbanking.modules.account.service.PixKeyService;
import com.quantumbanking.modules.shared.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account/pix")
public class PixKeyController {

    private final PixKeyService pixKeyService;

    @PostMapping("/register")
    public ResponseEntity<PixKeyResponseDTO> register(@AuthenticationPrincipal User user,
                                                      @RequestBody @Valid PixKeyRequestDTO requestDTO) {

        PixKeyResponseDTO response = pixKeyService.registerPixKey(user, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/keys")
    public ResponseEntity<List<PixKeyResponseDTO>> list(@AuthenticationPrincipal User user) {

        return ResponseEntity.ok(pixKeyService.listPixKey(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal User user,
                                       @PathVariable UUID id) {
        pixKeyService.removePixKey(user, id);
        return ResponseEntity.noContent().build();
    }
}
