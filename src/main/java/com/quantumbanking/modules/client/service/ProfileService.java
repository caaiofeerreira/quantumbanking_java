package com.quantumbanking.modules.client.service;

import com.quantumbanking.infra.exception.SameEmailException;
import com.quantumbanking.infra.exception.SamePhoneException;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.client.dto.ProfileResponse;
import com.quantumbanking.modules.client.repository.ClientRepository;
import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.dto.UpdateAddressRequestDTO;
import com.quantumbanking.modules.shared.service.validation.CepValidator;
import com.quantumbanking.modules.shared.service.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ClientService clientService;
    private final CompanyService companyService;

    private final UserValidator userValidator;
    private final CepValidator cepValidator;

    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {

        Client client = clientService.userAuthenticated(userId);
        return client.getType() == ClientType.JURIDICA
                ? companyService.getProfile(client)
                : clientService.getProfile(client);
    }

    @Transactional
    public void updatePhone(Long userId, String phone) {

        Client client = clientService.userAuthenticated(userId);

        String normalizedPhone = userValidator.normalizePhone(phone);

        if (normalizedPhone.equals(client.getPhone())) {
            throw new SamePhoneException("O telefone informado é o mesmo já cadastrado.");
        }

        userValidator.checkPhoneNotRegistered(normalizedPhone);

        client.updatePhone(normalizedPhone);
        clientRepository.save(client);
    }

    @Transactional
    public void updateEmail(Long userId, String email) {

        Client client = clientService.userAuthenticated(userId);

        String normalizedEmail = userValidator.normalizeEmail(email);

        if (normalizedEmail.equals(client.getEmail())) {
            throw new SameEmailException("O e-mail informado é o mesmo já cadastrado.");
        }
        userValidator.checkEmailNotRegistered(normalizedEmail);

        client.updateEmail(normalizedEmail);
        clientRepository.save(client);
    }

    @Transactional
    public void updateAddress(Long userId, UpdateAddressRequestDTO requestDTO) {

        String normalizedCep = cepValidator.normalizeCep(requestDTO.zipCode());

        Client client = clientService.userAuthenticated(userId);

        Address address = new Address(
                requestDTO.street(),
                requestDTO.number(),
                requestDTO.complement(),
                requestDTO.neighborhood(),
                requestDTO.city(),
                requestDTO.state().toUpperCase(),
                normalizedCep
        );

        client.updateAddress(address);
        clientRepository.save(client);
    }
}
