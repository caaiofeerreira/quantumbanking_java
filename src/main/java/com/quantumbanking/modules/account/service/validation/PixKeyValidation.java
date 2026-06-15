package com.quantumbanking.modules.account.service.validation;

import com.quantumbanking.infra.exception.PixKeyAlreadyExistsException;
import com.quantumbanking.infra.exception.PixKeyLimitException;
import com.quantumbanking.modules.account.repository.PixKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PixKeyValidation {

    private final PixKeyRepository pixKeyRepository;

    public void validatePixKey(Long keys, String key) {
        checkCountByAccountId(keys);
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