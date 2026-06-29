package com.quantumbanking.modules.bank.service;

import com.quantumbanking.infra.exception.AgencyNotFoundException;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.bank.dto.AgencyRegistrationDTO;
import com.quantumbanking.modules.bank.factory.AgencyFactory;
import com.quantumbanking.modules.bank.repository.AgencyRepository;
import com.quantumbanking.modules.bank.service.validation.AgencyValidator;
import com.quantumbanking.modules.shared.service.validation.CepValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgencyService {

    private final AgencyFactory agencyFactory;
    private final AgencyRepository agencyRepository;
    private final AgencyValidator agencyValidator;

    private final CepValidator cepValidator;

    public void save(Agency agency) {
        agencyRepository.save(agency);
    }

    @Transactional(readOnly = true)
    public Agency getAgencyByNumber(String agencyNumber) {
        return agencyRepository.findByAgencyNumber(agencyNumber)
                .orElseThrow(() -> new AgencyNotFoundException("Agência não encontrada."));
    }


    @Transactional(readOnly = true)
    public Long getAgencyIdByNumber(String agencyNumber) {
        return agencyRepository.findByAgencyNumber(agencyNumber)
                .map(Agency::getId)
                .orElseThrow(() -> new AgencyNotFoundException("Agência não encontrada."));
    }

    @Transactional(readOnly = true)
    public List<Agency> getAllAgencies() {
        return agencyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Agency getAgencyById(Long id) {
        return agencyRepository.findById(id)
                .orElseThrow(() -> new AgencyNotFoundException("Agência não encontrada."));
    }

    @Transactional(readOnly = true)
    public List<Agency> getAgenciesByIds(List<Long> ids) {
        return agencyRepository.findAllById(ids);
    }

    @Transactional
    public Agency createAgency(AgencyRegistrationDTO dto, Bank bank) {

        agencyValidator.checkAgencyNotRegistered(dto.agencyNumber());

        String normalizedPhone = agencyValidator.normalizePhone(dto.phone());
        String normalizedCep = cepValidator.normalizeCep(dto.address().zipCode());

        Agency agency = agencyFactory.createAgency(dto, normalizedPhone, normalizedCep, bank);

        save(agency);
        return agency;
    }
}
