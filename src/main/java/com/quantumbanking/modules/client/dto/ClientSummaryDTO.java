package com.quantumbanking.modules.client.dto;

import com.quantumbanking.modules.client.domain.ClientType;

public record ClientSummaryDTO(String name,
                               String document,
                               ClientType type) {
}
