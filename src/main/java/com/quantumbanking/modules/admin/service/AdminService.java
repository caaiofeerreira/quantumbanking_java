package com.quantumbanking.modules.admin.service;

import com.quantumbanking.infra.exception.AgencyAlreadyExistsException;
import com.quantumbanking.infra.exception.CpfAlreadyRegisteredException;
import com.quantumbanking.infra.exception.ResourceNotFoundException;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.bank.domain.manager.Manager;
import com.quantumbanking.modules.bank.dto.AgencyRegistrationDTO;
import com.quantumbanking.modules.bank.dto.AgencyResponseDTO;
import com.quantumbanking.modules.bank.dto.ManagerRegistrationDTO;
import com.quantumbanking.modules.bank.dto.ManagerResponseDTO;
import com.quantumbanking.modules.bank.mapper.AgencyMapper;
import com.quantumbanking.modules.bank.mapper.ManagerMapper;
import com.quantumbanking.modules.bank.repository.AgencyRepository;
import com.quantumbanking.modules.bank.repository.BankRepository;
import com.quantumbanking.modules.bank.repository.ManagerRepository;
import com.quantumbanking.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AgencyRepository agencyRepository;
    private final BankRepository bankRepository;
    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;

    private final AgencyMapper agencyMapper;
    private final ManagerMapper managerMapper;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AgencyResponseDTO registerAgency(AgencyRegistrationDTO dto) {

        Bank bank = bankRepository.findByBankCode(dto.bankCode())
                .orElseThrow(() ->  new ResourceNotFoundException("Banco com código " + dto.bankCode() + " não encontrado."));

        if (agencyRepository.existsByAgencyNumber(dto.agencyNumber())) {
            throw new AgencyAlreadyExistsException("A agência número " + dto.agencyNumber() + " já está cadastrada.");
        }

        Agency agency = new Agency(dto, bank);
        agencyRepository.save(agency);

        return agencyMapper.toAgencyResponseDTO(agency);
    }

    @Transactional
    public ManagerResponseDTO registerManager(ManagerRegistrationDTO dto) {

        if (userRepository.existsByCpf(dto.cpf())) {
            throw new CpfAlreadyRegisteredException("Já existe um usuário cadastrado com o CPF: " + dto.cpf());
        }

        Agency agency = agencyRepository.findByAgencyNumber(dto.agencyNumber())
                .orElseThrow(() -> {
                    String agencies = String.join(", ", agencyRepository.findAllAgencyNumbersAndCity());
                    return new ResourceNotFoundException("Agência não encontrada. Disponíveis: " + agencies);
                });

        String encryptedPassword = passwordEncoder.encode(dto.password());
        Manager manager = new Manager(dto, encryptedPassword, agency);
        managerRepository.save(manager);

        return managerMapper.toManagerResponseDTO(manager);
    }
}