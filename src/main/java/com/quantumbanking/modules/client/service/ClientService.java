package com.quantumbanking.modules.client.service;

import com.quantumbanking.infra.exception.IncompleteCompanyDataException;
import com.quantumbanking.infra.exception.UserNotFoundException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.factory.AccountFactory;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.service.AgencyService;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.client.domain.Company;
import com.quantumbanking.modules.client.dto.ClientProfileResponseDTO;
import com.quantumbanking.modules.client.dto.ClientRegistrationDTO;
import com.quantumbanking.modules.client.dto.ClientResponseDTO;
import com.quantumbanking.modules.shared.dto.UpdateAddressRequestDTO;
import com.quantumbanking.modules.client.factory.ClientFactory;
import com.quantumbanking.modules.client.mapper.ClientMapper;
import com.quantumbanking.modules.client.repository.ClientRepository;
import com.quantumbanking.modules.client.repository.CompanyRepository;
import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.domain.user.User;
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

    private final CompanyRepository companyRepository;

    private final AccountFactory accountFactory;

    private final PasswordEncoder passwordEncoder;

    private final UserValidator userValidator;
    private final CepValidator cepValidator;

    private Client userAuthenticated(Long userId) {
        return clientRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Cliente não encontrado."));
    }

    @Transactional
    public ClientResponseDTO registerClient(ClientRegistrationDTO requestDTO) {

        if (requestDTO.clientType() == ClientType.JURIDICA && requestDTO.company() == null) {
            throw new IncompleteCompanyDataException("Dados da empresa são obrigatórios para pessoa jurídica.");
        }

        String encryptedPassword = passwordEncoder.encode(requestDTO.password());

        Client client = clientFactory.createClient(
                requestDTO,
                encryptedPassword
        );
        clientRepository.save(client);

        Company company = null;

        if (requestDTO.clientType() == ClientType.JURIDICA) {
            company = new Company(requestDTO.company(), client);
            companyRepository.save(company);
        }

        Agency agency = agencyService.getAgencyByNumber(requestDTO.agencyNumber());

        Account account = accountFactory.createDefaultAccount(
                requestDTO.clientType(),
                requestDTO.accountType(),
                agency,
                client
        );

        accountService.save(account);

        return clientMapper.toClientResponseDTO(client,account, company);
    }

    @Transactional(readOnly = true)
    public ClientProfileResponseDTO getProfile(User user) {

        Client client = userAuthenticated(user.getId());
        return clientMapper.toProfileResponseDTO(client);
    }

    @Transactional
    public void updatePhone(User user, String phone) {

        String normalizedPhone = userValidator.normalizePhone(phone);

        Client client = userAuthenticated(user.getId());
        client.updatePhone(normalizedPhone);
        clientRepository.save(client);
    }

    @Transactional
    public void updateEmail(User user, String email) {

        String normalizedEmail = userValidator.normalizeEmail(email);

        Client client = userAuthenticated(user.getId());
        client.updateEmail(normalizedEmail);
        clientRepository.save(client);
    }

    @Transactional
    public void updateAddress(User user, UpdateAddressRequestDTO requestDTO) {

        String normalizedCep = cepValidator.normalizeCep(requestDTO.zipCode());

        Client client = userAuthenticated(user.getId());

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