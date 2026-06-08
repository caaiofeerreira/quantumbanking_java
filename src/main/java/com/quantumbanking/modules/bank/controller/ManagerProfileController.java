package com.quantumbanking.modules.bank.controller;

import com.quantumbanking.modules.bank.dto.ManagerProfileResponseDTO;
import com.quantumbanking.modules.bank.service.ManagerService;
import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.shared.dto.UpdateAddressRequestDTO;
import com.quantumbanking.modules.shared.dto.UpdateEmailRequestDTO;
import com.quantumbanking.modules.shared.dto.UpdatePhoneRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager/profile")
public class ManagerProfileController {

    private final ManagerService managerService;

    @GetMapping()
    public ResponseEntity<ManagerProfileResponseDTO> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(managerService.getProfile(user));
    }

    @PatchMapping("/phone")
    public ResponseEntity<Void> updatePhone(@AuthenticationPrincipal User user, @RequestBody @Valid UpdatePhoneRequestDTO requestDTO) {
        managerService.updatePhone(user, requestDTO.phone());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/email")
    public ResponseEntity<Void> updateEmail(@AuthenticationPrincipal User user, @RequestBody @Valid UpdateEmailRequestDTO requestDTO) {
        managerService.updateEmail(user, requestDTO.email());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/address")
    public ResponseEntity<Void> updateAddress(@AuthenticationPrincipal User user, @RequestBody @Valid UpdateAddressRequestDTO requestDTO) {
        managerService.updateAddress(user, requestDTO);
        return ResponseEntity.noContent().build();
    }
}