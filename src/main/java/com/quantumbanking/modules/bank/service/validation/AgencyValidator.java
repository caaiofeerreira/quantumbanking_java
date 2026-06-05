package com.quantumbanking.modules.bank.service.validation;

import com.quantumbanking.infra.exception.AgencyAlreadyExistsException;
import com.quantumbanking.infra.exception.AgencyNotFoundException;
import com.quantumbanking.modules.bank.repository.AgencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgencyValidator {

    private final AgencyRepository agencyRepository;

    public void checkAgencyExists(String agencyNumber) {
        if (!agencyRepository.existsByAgencyNumber(agencyNumber)) {
            throw new AgencyNotFoundException("A agência de número " + agencyNumber + " não foi encontrada.");
        }
    }

    public void checkAgencyNotRegistered(String agencyNumber) {
        if (agencyRepository.existsByAgencyNumber(agencyNumber)) {
            throw new AgencyAlreadyExistsException(" A agência de número " + agencyNumber + " já possui cadastro.");
        }
    }
}
