package com.quantumbanking.modules.account.service;

import com.quantumbanking.infra.exception.UnauthorizedAccessException;
import com.quantumbanking.infra.exception.ValidateException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.PixKey;
import com.quantumbanking.modules.account.dto.PixKeyRequestDTO;
import com.quantumbanking.modules.account.dto.PixKeyResponseDTO;
import com.quantumbanking.modules.account.mapper.PixKeyMapper;
import com.quantumbanking.modules.account.repository.PixKeyRepository;
import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.shared.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PixKeyService {

    private final PixKeyRepository pixRepository;

    private final PixKeyMapper pixKeyMapper;

    private final UserService userService;

    @Transactional
    public PixKeyResponseDTO registerPixKey(User user, PixKeyRequestDTO requestDTO) {

        Account account = userService
                .getAuthenticatedUserAccount(user.getId());

        if (pixRepository.countByAccountId(account.getId()) >= 5) {
            throw new ValidateException("Limite de 5 chaves Pix atingido.");
        }

        if (pixRepository.existsByKey(requestDTO.key())) {
            throw new ValidateException("Chave Pix já cadastrada.");
        }

        PixKey pixKey = new PixKey(
                requestDTO.key().toLowerCase(),
                requestDTO.type(),
                account);

        pixRepository.save(pixKey);

        return pixKeyMapper.toPixKeyResponseDTO(pixKey);
    }

    public List<PixKeyResponseDTO> listPixKey(User user) {

        Account account = userService
                .getAuthenticatedUserAccount(user.getId());

        return account.getPixKeys()
                .stream()
                .map(pixKeyMapper::toPixKeyResponseDTO)
                .toList();
    }

    @Transactional
    public void removePixKey(User user, UUID pixKeyId) {

        PixKey pixKey = pixRepository.findById(pixKeyId)
                .orElseThrow(() -> new ValidateException("Chave Pix não encontrada."));

        if (!pixKey.getAccount().getClient().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("Você não tem permissão para deletar essa chave.");
        }

        pixRepository.delete(pixKey);
    }
}