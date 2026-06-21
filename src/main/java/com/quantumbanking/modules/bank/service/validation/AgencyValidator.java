package com.quantumbanking.modules.bank.service.validation;

import com.quantumbanking.infra.exception.AgencyAlreadyExistsException;
import com.quantumbanking.infra.exception.AgencyNotFoundException;
import com.quantumbanking.infra.exception.InvalidPhoneException;
import com.quantumbanking.modules.bank.repository.AgencyRepository;
import com.quantumbanking.modules.shared.util.FormattingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class AgencyValidator {

    private final AgencyRepository agencyRepository;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\(?\\d{2}\\)?[\\s-]?9?\\d{4}[-\\s]?\\d{4}$");

    public String normalizePhone(String phone) {

        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new InvalidPhoneException("Telefone inválido: " + phone);
        }
        return FormattingUtils.normalizePhone(phone);
    }

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