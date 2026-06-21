package com.quantumbanking.modules.pixKey.mapper;

import com.quantumbanking.modules.pixKey.domain.PixKey;
import com.quantumbanking.modules.pixKey.dto.PixKeyResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class PixKeyMapper {

    public PixKeyResponseDTO toPixKeyResponseDTO(PixKey pixKey) {
        return new PixKeyResponseDTO(
                pixKey.getKey(),
                pixKey.getType()
        );
    }
}
