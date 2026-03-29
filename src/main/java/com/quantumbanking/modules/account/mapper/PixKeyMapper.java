package com.quantumbanking.modules.account.mapper;

import com.quantumbanking.modules.account.domain.PixKey;
import com.quantumbanking.modules.account.dto.PixKeyResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class PixKeyMapper {

    public PixKeyResponseDTO toResponseDTO(PixKey pixKey) {
        return new PixKeyResponseDTO(
                pixKey.getId(),
                pixKey.getKey(),
                pixKey.getType()
        );
    }
}
