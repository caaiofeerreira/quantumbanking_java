package com.quantumbanking.modules.pixKey.service;

import com.quantumbanking.infra.exception.ResourceNotFoundException;
import com.quantumbanking.infra.exception.UnauthorizedAccessException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.pixKey.detector.PixKeyDetector;
import com.quantumbanking.modules.pixKey.domain.PixKey;
import com.quantumbanking.modules.pixKey.dto.PixKeyRequestDTO;
import com.quantumbanking.modules.pixKey.dto.PixKeyResponseDTO;
import com.quantumbanking.modules.pixKey.mapper.PixKeyMapper;
import com.quantumbanking.modules.pixKey.repository.PixKeyRepository;
import com.quantumbanking.modules.pixKey.service.validation.PixKeyValidator;
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
    private final PixKeyValidator pixKeyValidation;

    public Optional<PixKey> getPixKey(String key) {
        return pixKeyRepository.findByKey(key);
    }

    @Transactional
    public PixKeyResponseDTO registerPixKey(Long userId, String accountNumber, PixKeyRequestDTO requestDTO) {

        Account account = accountService.getAuthenticatedUserAccount(userId, accountNumber);

        PixKeyDetector.PixKeyDetectionResult detectionResult = PixKeyDetector.checkAndDetectKey(requestDTO.key());

        pixKeyValidation.validatePixKey(
                detectionResult.type(),
                detectionResult.normalizedKey(),
                account.getClient(),
                account.getId()
        );

        PixKey pixKey = new PixKey(
                detectionResult.normalizedKey(),
                detectionResult.type(),
                account
        );

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