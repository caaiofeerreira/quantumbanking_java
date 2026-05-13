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
import com.quantumbanking.modules.shared.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PixKeyService {

    private final AccountService accountService;

    private final PixKeyRepository pixKeyRepository;
    private final PixKeyMapper pixKeyMapper;

    public Optional<PixKey> findByKey(String key) {
        return pixKeyRepository.findByKey(key);
    }

    @Transactional
    public PixKeyResponseDTO registerPixKey(User user, PixKeyRequestDTO requestDTO) {

        Account account = accountService
                .getAuthenticatedUserAccount(user.getId());

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

    public List<PixKeyResponseDTO> listPixKey(User user) {

        Account account = accountService
                .getAuthenticatedUserAccount(user.getId());

        return account.getPixKeys()
                .stream()
                .map(pixKeyMapper::toPixKeyResponseDTO)
                .toList();
    }

    @Transactional
    public void removePixKey(User user, UUID pixKeyId) {

        PixKey pixKey = pixKeyRepository.findById(pixKeyId)
                .orElseThrow(() -> new ResourceNotFoundException("Chave Pix não encontrada."));

        if (!pixKey.getAccount().getClient().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("Você não tem permissão para deletar essa chave.");
        }

        pixKeyRepository.delete(pixKey);
    }
}