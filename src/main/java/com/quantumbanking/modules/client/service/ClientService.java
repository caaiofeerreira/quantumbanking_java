package com.quantumbanking.modules.client.service;

import com.quantumbanking.infra.exception.UserNotFoundException;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.service.AgencyService;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.client.dto.*;
import com.quantumbanking.modules.shared.dto.NormalizedUserData;
import com.quantumbanking.modules.client.factory.ClientFactory;
import com.quantumbanking.modules.client.mapper.ClientMapper;
import com.quantumbanking.modules.client.repository.ClientRepository;
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

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ClientFactory clientFactory;

    private final PasswordEncoder passwordEncoder;

    private final UserValidator userValidator;
    private final CepValidator cepValidator;

    public Client userAuthenticated(Long userId) {
        return clientRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Cliente não encontrado."));
    }

    public Client createClient(ClientCoreDataDTO data, ClientType clientType) {

        userValidator.checkCpfNotRegistered(data.cpf());
        userValidator.checkEmailNotRegistered(data.email());
        userValidator.checkPhoneNotRegistered(data.phone());

        NormalizedUserData normalized = new NormalizedUserData(
                data.name(),
                userValidator.normalizeCpf(data.cpf()),
                userValidator.normalizePhone(data.phone()),
                userValidator.normalizeEmail(data.email()),
                passwordEncoder.encode(data.password()),
                data.address().street(),
                data.address().number(),
                data.address().complement(),
                data.address().neighborhood(),
                data.address().city(),
                data.address().state().toUpperCase(),
                cepValidator.normalizeCep(data.address().zipCode())
        );

        Client client = clientFactory.createClient(normalized, clientType);
        clientRepository.save(client);

        return client;
    }

    @Transactional
    public void registerFisica(FisicaRegistrationDTO dto) {

        Client client = createClient(dto.data(), ClientType.FISICA);
        Agency agency = agencyService.getAgencyByNumber(dto.agencyNumber());

        accountService.openInitialAccount(
                dto.accountType(),
                agency,
                client,
                null
        );
    }

    public ClientProfileResponseDTO getProfile(Client client) {
        return clientMapper.toProfileResponseDTO(client);
    }
}