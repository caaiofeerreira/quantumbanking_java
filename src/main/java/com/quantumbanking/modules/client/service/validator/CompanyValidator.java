package com.quantumbanking.modules.client.service.validator;

import com.quantumbanking.infra.exception.CnpjAlreadyRegisteredException;
import com.quantumbanking.infra.exception.IncompleteCompanyDataException;
import com.quantumbanking.infra.exception.InvalidCompanyDataException;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.client.domain.Company;
import com.quantumbanking.modules.client.dto.CompanyRegistrationDTO;
import com.quantumbanking.modules.client.repository.CompanyRepository;
import com.quantumbanking.modules.shared.util.FormattingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyValidator {

    private final CompanyRepository companyRepository;

    public void checkCompanyRequiredForAccount(ClientType clientType, Company company) {
        if (clientType == ClientType.JURIDICA && company == null) {
            throw new IncompleteCompanyDataException("Cliente jurídico requer uma empresa associada.");
        }

        if (clientType != ClientType.JURIDICA && company != null) {
            throw new InvalidCompanyDataException("Cliente pessoa física não deve estar associado a uma empresa.");
        }
    }

    public void checkCompanyDataConsistency(ClientType clientType, CompanyRegistrationDTO companyDto) {
        if (clientType != ClientType.JURIDICA) {
            if (companyDto != null) {
                throw new InvalidCompanyDataException("Ao passar dados da empresa, é necessário que o cliente seja do tipo jurídico.");
            }
            return;
        }
        if (companyDto == null) {
            throw new IncompleteCompanyDataException("Dados da empresa são obrigatórios para pessoa jurídica.");
        }
    }

    public void checkCnpjValid(String cnpj) {
        if (!FormattingUtils.isValidCnpj(cnpj)) {
            throw new InvalidCompanyDataException("CNPJ inválido: " + cnpj);
        }
    }

    public void checkCnpjNotRegistered(String normalizedCnpj) {
        if (companyRepository.existsByCnpj(normalizedCnpj)) {
            throw new CnpjAlreadyRegisteredException("Não foi possível concluir o cadastro com os dados informados.");
        }
    }

    public void checkCompanyName(String companyName) {

        if (companyRepository.existsByCompanyName(companyName)) {
            throw new InvalidCompanyDataException(
                    "A razão social '" + companyName + "' já está cadastrada no sistema."
            );
        }
    }
}