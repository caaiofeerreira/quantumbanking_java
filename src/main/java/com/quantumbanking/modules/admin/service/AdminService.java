package com.quantumbanking.modules.admin.service;

import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.bank.domain.manager.Manager;
import com.quantumbanking.modules.bank.dto.AgencyRegistrationDTO;
import com.quantumbanking.modules.bank.dto.AgencyResponseDTO;
import com.quantumbanking.modules.bank.dto.ManagerRegistrationDTO;
import com.quantumbanking.modules.bank.dto.ManagerResponseDTO;
import com.quantumbanking.modules.bank.mapper.AgencyMapper;
import com.quantumbanking.modules.bank.mapper.ManagerMapper;
import com.quantumbanking.modules.bank.service.AgencyService;
import com.quantumbanking.modules.bank.service.BankService;
import com.quantumbanking.modules.bank.service.ManagerService;
import com.quantumbanking.modules.shared.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AgencyService agencyService;
    private final BankService bankService;
    private final ManagerService managerService;
    private final UserService userService;

    private final AgencyMapper agencyMapper;
    private final ManagerMapper managerMapper;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AgencyResponseDTO registerAgency(AgencyRegistrationDTO dto) {

        Bank bank = bankService.getBankByCode(dto.bankCode());

        agencyService.validateAgencyNotRegistered(dto.agencyNumber());

        Agency agency = new Agency(dto, bank);
        agencyService.save(agency);

        return agencyMapper.toAgencyResponseDTO(agency);
    }

    @Transactional
    public ManagerResponseDTO registerManager(ManagerRegistrationDTO dto) {

        userService.validateCpfNotRegistered(dto.cpf());

        Agency agency = agencyService.getAgencyByNumber(dto.agencyNumber());

        String encryptedPassword = passwordEncoder.encode(dto.password());

        Manager manager = new Manager(dto, encryptedPassword, agency);
        managerService.save(manager);

        return managerMapper.toManagerResponseDTO(manager);
    }
}