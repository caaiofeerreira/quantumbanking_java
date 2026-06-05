package com.quantumbanking.modules.bank.service;

import com.quantumbanking.infra.exception.AgencyNotFoundException;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.repository.AgencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgencyService {

    private final AgencyRepository agencyRepository;

    public Agency getAgencyByNumber(String agencyNumber) {
        return agencyRepository.findByAgencyNumber(agencyNumber)
                .orElseThrow(() -> new AgencyNotFoundException("Agência não encontrada."));
    }

    public List<Agency> getAllAgencies() {
        return agencyRepository.findAll();
    }

    public void save(Agency agency) {
        agencyRepository.save(agency);
    }
}
