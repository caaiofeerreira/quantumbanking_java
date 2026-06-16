package com.quantumbanking.modules.client.service;

import com.quantumbanking.infra.exception.UserNotFoundException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.service.AgencyService;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.Company;
import com.quantumbanking.modules.client.dto.ClientProfileResponseDTO;
import com.quantumbanking.modules.client.dto.ClientRegistrationDTO;
import com.quantumbanking.modules.client.dto.ClientResponseDTO;
import com.quantumbanking.modules.shared.dto.NormalizedUserData;
import com.quantumbanking.modules.shared.dto.UpdateAddressRequestDTO;
import com.quantumbanking.modules.client.factory.ClientFactory;
import com.quantumbanking.modules.client.mapper.ClientMapper;
import com.quantumbanking.modules.client.repository.ClientRepository;
import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.service.validation.CepValidator;
import com.quantumbanking.modules.shared.service.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final AccountService accountService;
    private final AgencyService agencyService;
    private final CompanyService companyService;

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ClientFactory clientFactory;

    private final PasswordEncoder passwordEncoder;

    private final UserValidator userValidator;
    private final CepValidator cepValidator;

    private Client userAuthenticated(Long userId) {
        return clientRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Cliente não encontrado."));
    }

    private Client createClient(ClientRegistrationDTO dto) {

        userValidator.checkCpfNotRegistered(dto.cpf());
        userValidator.checkEmailNotRegistered(dto.email());

        NormalizedUserData data = new NormalizedUserData(
                dto.name(),
                userValidator.normalizeCpf(dto.cpf()),
                userValidator.normalizePhone(dto.phone()),
                userValidator.normalizeEmail(dto.email()),
                passwordEncoder.encode(dto.password()),
                dto.address().street(),
                dto.address().number(),
                dto.address().complement(),
                dto.address().neighborhood(),
                dto.address().city(),
                dto.address().state().toUpperCase(),
                cepValidator.normalizeCep(dto.address().zipCode())
        );

        Client client = clientFactory.createClient(
                data,
                dto.clientType()
        );
        clientRepository.save(client);

        return client;
    }

    @Transactional
    public ClientResponseDTO registerClient(ClientRegistrationDTO dto) {

        Client client = createClient(dto);
        Company company = companyService.createIfApplicable(dto, client);
        Agency agency =agencyService.getAgencyByNumber(dto.agencyNumber());

        Account account = accountService.openInitialAccount(
                client.getType(),
                dto.accountType(),
                agency,
                client
        );

        return clientMapper.toClientResponseDTO(client, account, company);
    }

    @Transactional(readOnly = true)
    public ClientProfileResponseDTO getProfile(Long userId) {

        Client client = userAuthenticated(userId);
        Company company = companyService.findByClient(client).orElse(null);
        return clientMapper.toProfileResponseDTO(client, company);
    }

    @Transactional
    public void updatePhone(Long userId, String phone) {

        String normalizedPhone = userValidator.normalizePhone(phone);

        Client client = userAuthenticated(userId);
        client.updatePhone(normalizedPhone);
        clientRepository.save(client);
    }

    @Transactional
    public void updateEmail(Long userId, String email) {

        String normalizedEmail = userValidator.normalizeEmail(email);

        Client client = userAuthenticated(userId);
        client.updateEmail(normalizedEmail);
        clientRepository.save(client);
    }

    @Transactional
    public void updateAddress(Long userId, UpdateAddressRequestDTO requestDTO) {

        String normalizedCep = cepValidator.normalizeCep(requestDTO.zipCode());

        Client client = userAuthenticated(userId);

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