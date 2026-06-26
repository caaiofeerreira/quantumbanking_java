package com.quantumbanking.modules.pixKey.service.validation;

import com.quantumbanking.infra.exception.InvalidPixKeyTypeException;
import com.quantumbanking.infra.exception.PixKeyAlreadyExistsException;
import com.quantumbanking.infra.exception.PixKeyLimitException;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.pixKey.domain.PixKeyType;
import com.quantumbanking.modules.pixKey.repository.PixKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PixKeyValidator {

    private final PixKeyRepository pixKeyRepository;

    public void validatePixKey(PixKeyType type, String key, Client client, Long accountId) {
        checkKeyOwnership(type, key, client);
        checkCountByAccountId(accountId);
        checkKeyAlreadyExists(key);
    }

    private void checkKeyOwnership(PixKeyType type, String normalizedKey, Client client) {
        switch (type) {
            case CPF -> {
                if (!normalizedKey.equals(client.getCpf())) {
                    throw new InvalidPixKeyTypeException("Não é permitido cadastrar o CPF de terceiros como chave Pix.");
                }
            }
            case EMAIL -> {
                if (!normalizedKey.equalsIgnoreCase(client.getEmail())) {
                    throw new InvalidPixKeyTypeException("Não é permitido cadastrar o e-mail de terceiros como chave Pix.");
                }
            }
            case PHONE -> {
                if (!normalizedKey.equals(client.getPhone())) {
                    throw new InvalidPixKeyTypeException("Não é permitido cadastrar o celular de terceiros como chave Pix.");
                }
            }
        }
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