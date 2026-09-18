package com.quantumbanking.modules.client.controller;

import com.quantumbanking.modules.client.dto.ProfileResponse;
import com.quantumbanking.modules.client.service.ProfileService;
import com.quantumbanking.modules.shared.dto.UpdateAddressRequestDTO;
import com.quantumbanking.modules.shared.dto.UpdateEmailRequestDTO;
import com.quantumbanking.modules.shared.dto.UpdatePhoneRequestDTO;
import com.quantumbanking.modules.shared.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(profileService.getProfile(user.getId()));
    }

    @PatchMapping("/phone")
    public ResponseEntity<Void> updatePhone(@AuthenticationPrincipal User user,
                                            @RequestBody @Valid UpdatePhoneRequestDTO dto) {

        profileService.updatePhone(user.getId(), dto.phone());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/email")
    public ResponseEntity<Void> updateEmail(@AuthenticationPrincipal User user,
                                            @RequestBody @Valid UpdateEmailRequestDTO dto) {

        profileService.updateEmail(user.getId(), dto.email());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/address")
    public ResponseEntity<Void> updateAddress(@AuthenticationPrincipal User user,
                                              @RequestBody @Valid UpdateAddressRequestDTO dto) {

        profileService.updateAddress(user.getId(), dto);
        return ResponseEntity.noContent().build();
    }
}