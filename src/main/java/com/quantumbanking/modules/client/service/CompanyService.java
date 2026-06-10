package com.quantumbanking.modules.client.service;

import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.Company;
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
    public Company registerCompany(CompanyRegistrationDTO registrationDTO, Client client) {
        Company company = companyFactory.createCompany(registrationDTO, client);
        return companyRepository.save(company);
    }
}