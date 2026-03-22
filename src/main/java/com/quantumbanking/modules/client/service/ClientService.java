package com.quantumbanking.modules.client.service;

import com.quantumbanking.infra.exception.ValidateException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.factory.AccountFactory;
import com.quantumbanking.modules.account.repository.AccountRepository;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.repository.AgencyRepository;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.client.domain.Company;
import com.quantumbanking.modules.client.dto.ClientRegistrationDTO;
import com.quantumbanking.modules.client.dto.ClientResponseDTO;
import com.quantumbanking.modules.client.mapper.ClientMapper;
import com.quantumbanking.modules.client.repository.ClientRepository;
import com.quantumbanking.modules.client.repository.CompanyRepository;
import com.quantumbanking.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AgencyRepository agencyRepository;
    private final CompanyRepository companyRepository;

    private final AccountFactory accountFactory;

    private final ClientMapper clientMapper;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ClientResponseDTO clientRegister(ClientRegistrationDTO requestDTO) {

        if (userRepository.existsByCpf(requestDTO.cpf())) {
            throw new ValidateException("CPF já cadastrado.");
        }

        if (requestDTO.clientType() == ClientType.JURIDICA && requestDTO.company() == null) {
            throw new ValidateException("Dados da empresa são obrigatórios para pessoa jurídica.");
        }

        Agency agency = agencyRepository.findByNumber(requestDTO.agencyNumber())
                .orElseThrow(() -> {
                    String agencies = String.join(", ", agencyRepository.findAllAgencyNumbersAndCidade());
                    return new ValidateException("Agência não encontrada. Disponíveis: " + agencies);
                });

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
                agency
        );

        account.setClient(client);
        accountRepository.save(account);

        return clientMapper.toResponseDTO(client,account, company);
    }
}
