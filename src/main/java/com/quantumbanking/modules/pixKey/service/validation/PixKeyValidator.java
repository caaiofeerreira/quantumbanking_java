package com.quantumbanking.modules.pixKey.service.validation;

import com.quantumbanking.infra.exception.PixKeyAlreadyExistsException;
import com.quantumbanking.infra.exception.PixKeyLimitException;
import com.quantumbanking.modules.pixKey.repository.PixKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PixKeyValidator {

    private final PixKeyRepository pixKeyRepository;

    public void validatePixKey(Long accountId, String key) {
        checkCountByAccountId(accountId);
        checkKeyAlreadyExists(key);
    }

    private void checkCountByAccountId(Long accountId) {

        if (pixKeyRepository.countByAccountId(accountId) >= 5) {
            throw new PixKeyLimitException("Limite de 5 chaves Pix atingido.");
        }
    }

    private void checkKeyAlreadyExists(String key) {

        if (pixKeyRepository.existsByKey(key)) {
            throw new PixKeyAlreadyExistsException("Chave Pix já cadastrada.");
        }
    }
}