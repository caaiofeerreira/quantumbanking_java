package com.quantumbanking.modules.manager.dto;

import com.quantumbanking.modules.shared.domain.user.UserStatus;

public record ManagerBasicViewDTO(Long id,
                                  String name,
                                  String email,
                                  String phone,
                                  UserStatus status) {
}