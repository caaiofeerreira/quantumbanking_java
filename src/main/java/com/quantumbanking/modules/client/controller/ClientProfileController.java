package com.quantumbanking.modules.client.controller;

import com.quantumbanking.modules.client.dto.ClientProfileResponseDTO;
import com.quantumbanking.modules.shared.dto.UpdateAddressRequestDTO;
import com.quantumbanking.modules.shared.dto.UpdateEmailRequestDTO;
import com.quantumbanking.modules.shared.dto.UpdatePhoneRequestDTO;
import com.quantumbanking.modules.client.service.ClientService;
import com.quantumbanking.modules.shared.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account/profile")
public class ClientProfileController {

    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<ClientProfileResponseDTO> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clientService.getProfile(user));
    }

    @PatchMapping("/phone")
    public ResponseEntity<Void> updatePhone(@AuthenticationPrincipal User user, @RequestBody @Valid UpdatePhoneRequestDTO dto) {
        clientService.updatePhone(user, dto.phone());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/email")
    public ResponseEntity<Void> updateEmail(@AuthenticationPrincipal User user, @RequestBody @Valid UpdateEmailRequestDTO dto) {
        clientService.updateEmail(user, dto.email());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/address")
    public ResponseEntity<Void> updateAddress(@AuthenticationPrincipal User user, @RequestBody @Valid UpdateAddressRequestDTO dto) {
        clientService.updateAddress(user, dto);
        return ResponseEntity.noContent().build();
    }
}