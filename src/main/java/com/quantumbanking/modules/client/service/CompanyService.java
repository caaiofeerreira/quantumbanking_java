package com.quantumbanking.modules.client.service;

import com.quantumbanking.infra.exception.IncompleteCompanyDataException;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.client.domain.Company;
import com.quantumbanking.modules.client.dto.ClientRegistrationDTO;
import com.quantumbanking.modules.client.dto.CompanyRegistrationDTO;
import com.quantumbanking.modules.client.factory.CompanyFactory;
import com.quantumbanking.modules.client.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyFactory companyFactory;
    private final CompanyRepository companyRepository;

    public Optional<Company> findByClient(Client client) {
        return companyRepository.findByClient(client);
    }

    @Transactional
    public Company createIfApplicable(ClientRegistrationDTO dto, Client client) {

        if (dto.clientType() != ClientType.JURIDICA) return null;

        if (dto.company() == null) {
            throw new IncompleteCompanyDataException("Dados da empresa são obrigatórios para pessoa jurídica.");
        }

        Company company = companyFactory.createCompany(dto.company(), client);
        companyRepository.save(company);
        return company;
    }
}