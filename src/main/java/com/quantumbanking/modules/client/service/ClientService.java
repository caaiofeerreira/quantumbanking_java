package com.quantumbanking.modules.client.service;

import com.quantumbanking.infra.exception.IncompleteCompanyDataException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.factory.AccountFactory;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.service.AgencyService;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.client.domain.Company;
import com.quantumbanking.modules.client.dto.ClientRegistrationDTO;
import com.quantumbanking.modules.client.dto.ClientResponseDTO;
import com.quantumbanking.modules.client.mapper.ClientMapper;
import com.quantumbanking.modules.client.repository.ClientRepository;
import com.quantumbanking.modules.client.repository.CompanyRepository;
import com.quantumbanking.modules.shared.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final AccountService accountService;
    private final AgencyService agencyService;
    private final UserService userService;

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    private final CompanyRepository companyRepository;

    private final AccountFactory accountFactory;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ClientResponseDTO registerClient(ClientRegistrationDTO requestDTO) {

        userService.validateCpfNotRegistered(requestDTO.cpf());

        if (requestDTO.clientType() == ClientType.JURIDICA && requestDTO.company() == null) {
            throw new IncompleteCompanyDataException("Dados da empresa são obrigatórios para pessoa jurídica.");
        }

        Agency agency = agencyService.getAgencyByNumber(requestDTO.agencyNumber());

        String encryptedPassword = passwordEncoder.encode(requestDTO.password());
        Client client = new Client(requestDTO, encryptedPassword);
        clientRepository.save(client);

        Company company = null;

        if (requestDTO.clientType() == ClientType.JURIDICA) {
            company = new Company(requestDTO.company(), client);
            companyRepository.save(company);
        }

        Account account = accountFactory.createDefaultAccount(
                requestDTO.clientType(),
                requestDTO.accountType(),
                agency,
                client
        );

        accountService.save(account);

        return clientMapper.toClientResponseDTO(client,account, company);
    }
}