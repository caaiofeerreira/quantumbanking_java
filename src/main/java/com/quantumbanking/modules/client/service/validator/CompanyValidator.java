package com.quantumbanking.modules.client.service.validator;

import com.quantumbanking.infra.exception.IncompleteCompanyDataException;
import com.quantumbanking.infra.exception.InvalidCompanyDataException;
import com.quantumbanking.modules.account.domain.AccountType;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.client.domain.Company;
import com.quantumbanking.modules.client.dto.CompanyRegistrationDTO;
import org.springframework.stereotype.Component;

@Component
public class CompanyValidator {

    public void checkCompanyRequiredForAccount(AccountType accountType, Company company) {
        if (accountType == AccountType.JURIDICA && company == null) {
            throw new IncompleteCompanyDataException("Conta jurídica requer uma empresa associada.");
        }

        if (accountType != AccountType.JURIDICA && company != null) {
            throw new InvalidCompanyDataException("Conta física não deve estar associada a uma empresa.");
        }
    }

    public void checkCompanyDataConsistency(ClientType clientType, CompanyRegistrationDTO companyDto) {
        if (clientType != ClientType.JURIDICA) {
            if (companyDto != null) {
                throw new InvalidCompanyDataException("Ao passar dados da empresa, é necessário que o cliente e a conta sejam do tipo jurídica.");
            }
            return;
        }
        if (companyDto == null) {
            throw new IncompleteCompanyDataException("Dados da empresa são obrigatórios para pessoa jurídica.");
        }
    }
}
