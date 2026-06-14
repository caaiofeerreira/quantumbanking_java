package com.quantumbanking.modules.account.service;

import com.quantumbanking.infra.exception.PixKeyAlreadyExistsException;
import com.quantumbanking.infra.exception.PixKeyLimitException;
import com.quantumbanking.infra.exception.ResourceNotFoundException;
import com.quantumbanking.infra.exception.UnauthorizedAccessException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.PixKey;
import com.quantumbanking.modules.account.dto.PixKeyRequestDTO;
import com.quantumbanking.modules.account.dto.PixKeyResponseDTO;
import com.quantumbanking.modules.account.mapper.PixKeyMapper;
import com.quantumbanking.modules.account.repository.PixKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PixKeyService {

    private final AccountService accountService;

    private final PixKeyRepository pixKeyRepository;
    private final PixKeyMapper pixKeyMapper;

    public Optional<PixKey> getKey(String key) {
        return pixKeyRepository.findByKey(key);
    }

    @Transactional
    public PixKeyResponseDTO registerPixKey(Long userId, String accountNumber, PixKeyRequestDTO requestDTO) {

        Account account = accountService
                .getAuthenticatedUserAccount(userId, accountNumber);

        if (pixKeyRepository.countByAccountId(account.getId()) >= 5) {
            throw new PixKeyLimitException("Limite de 5 chaves Pix atingido.");
        }

        if (pixKeyRepository.existsByKey(requestDTO.key())) {
            throw new PixKeyAlreadyExistsException("Chave Pix já cadastrada.");
        }

        PixKey pixKey = new PixKey(
                requestDTO.key().toLowerCase(),
                requestDTO.type(),
                account);

        pixKeyRepository.save(pixKey);

        return pixKeyMapper.toPixKeyResponseDTO(pixKey);
    }

    @Transactional(readOnly = true)
    public List<PixKeyResponseDTO> listPixKey(Long userId , String accountNumber) {

        Account account = accountService
                .getAuthenticatedUserAccount(userId, accountNumber);

        return account.getPixKeys()
                .stream()
                .map(pixKeyMapper::toPixKeyResponseDTO)
                .toList();
    }

    @Transactional
    public void removePixKey(Long userId, String key) {

        PixKey pixKey = pixKeyRepository.findByKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Chave Pix não encontrada."));

        if (!pixKey.getAccount().getClient().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Você não tem permissão para deletar essa chave.");
        }

        pixKeyRepository.delete(pixKey);
    }
}