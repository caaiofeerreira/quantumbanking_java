package com.quantumbanking.modules.bank.service;

import com.quantumbanking.infra.exception.ValidateException;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.bank.dto.AgencyRegistrationDTO;
import com.quantumbanking.modules.bank.dto.AgencyResponseDTO;
import com.quantumbanking.modules.bank.mapper.AgencyMapper;
import com.quantumbanking.modules.bank.repository.AgencyRepository;
import com.quantumbanking.modules.bank.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgencyService {

    private final AgencyRepository agencyRepository;
    private final BankRepository bankRepository;

    private final AgencyMapper agencyMapper;

    @Transactional
    public AgencyResponseDTO createAgency(AgencyRegistrationDTO dto) {

        Bank bank = bankRepository.findByBankCode(dto.bankCode())
                .orElseThrow(() -> new ValidateException("Banco não encontrado."));

        if (agencyRepository.existsByAgencyNumber(dto.agencyNumber())) {
            throw new ValidateException("Agência já cadastrada.");
        }

        Agency agency = new Agency(dto, bank);
        agencyRepository.save(agency);

        return agencyMapper.toResponseDTO(agency);
    }
}