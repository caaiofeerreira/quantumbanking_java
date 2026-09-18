package com.quantumbanking.modules.client.service;

import com.quantumbanking.infra.exception.AccountNotFoundException;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.service.AgencyService;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.client.domain.Company;
import com.quantumbanking.modules.client.dto.CompanyProfileResponseDTO;
import com.quantumbanking.modules.client.dto.CompanyRegistrationDTO;
import com.quantumbanking.modules.client.dto.JuridicaRegistrationDTO;
import com.quantumbanking.modules.client.factory.CompanyFactory;
import com.quantumbanking.modules.client.mapper.CompanyMapper;
import com.quantumbanking.modules.client.repository.CompanyRepository;
import com.quantumbanking.modules.client.service.validator.CompanyValidator;
import com.quantumbanking.modules.shared.service.validation.CepValidator;
import com.quantumbanking.modules.shared.util.FormattingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyFactory companyFactory;
    private final CompanyRepository companyRepository;
    private final CompanyValidator companyValidator;
    private final CompanyMapper companyMapper;

    private final CepValidator cepValidator;

    private final ClientService clientService;
    private final AgencyService agencyService;
    private final AccountService accountService;

    public Optional<Company> findByClient(Client client) {
        return companyRepository.findByClient(client);
    }

    @Transactional
    public void registerJuridica(JuridicaRegistrationDTO dto) {

        Client client = clientService.createClient(dto.data(), ClientType.JURIDICA);
        Agency agency = agencyService.getAgencyByNumber(dto.agencyNumber());
        Company company = create(dto.company(), client);

        accountService.openInitialAccount(
                dto.accountType(),
                agency,
                client,
                company
        );
    }

    private Company create(CompanyRegistrationDTO dto, Client client) {

        companyValidator.checkCompanyDataConsistency(client.getType(), dto);
        companyValidator.checkCompanyName(dto.companyName());

        String normalizedCnpj = FormattingUtils.normalizeCnpj(dto.cnpj());
        companyValidator.checkCnpjValid(normalizedCnpj);
        companyValidator.checkCnpjNotRegistered(normalizedCnpj);

        String normalizedCep = cepValidator.normalizeCep(dto.address().zipCode());

        Company company = companyFactory.createCompany(dto, normalizedCep, normalizedCnpj, client);
        companyRepository.save(company);
        return company;
    }

    public CompanyProfileResponseDTO getProfile(Client client) {
        Company company = findByClient(client)
                .orElseThrow(() -> new AccountNotFoundException("Conta jurídica não encontrada para este usuário."));
        return companyMapper.toProfileResponseDTO(client, company);
    }
}