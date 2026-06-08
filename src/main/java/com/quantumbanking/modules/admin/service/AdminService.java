package com.quantumbanking.modules.admin.service;

import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.bank.domain.manager.Manager;
import com.quantumbanking.modules.bank.dto.*;
import com.quantumbanking.modules.bank.factory.AgencyFactory;
import com.quantumbanking.modules.bank.factory.ManagerFactory;
import com.quantumbanking.modules.bank.mapper.AgencyMapper;
import com.quantumbanking.modules.bank.mapper.ManagerMapper;
import com.quantumbanking.modules.bank.service.AgencyService;
import com.quantumbanking.modules.bank.service.BankService;
import com.quantumbanking.modules.bank.service.ManagerService;
import com.quantumbanking.modules.bank.service.validation.AgencyValidator;
import com.quantumbanking.modules.shared.service.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AgencyService agencyService;
    private final BankService bankService;
    private final ManagerService managerService;

    private final AgencyMapper agencyMapper;
    private final ManagerMapper managerMapper;

    private final AgencyFactory agencyFactory;
    private final ManagerFactory managerFactory;

    private final PasswordEncoder passwordEncoder;

    private final UserValidator userValidator;
    private final AgencyValidator agencyValidator;

    @Transactional
    public AgencyResponseDTO registerAgency(AgencyRegistrationDTO dto) {

        Bank bank = bankService.getBankByCode(dto.compe());

        agencyValidator.checkAgencyNotRegistered(dto.agencyNumber());

        Agency agency = agencyFactory.createAgency(dto, bank);
        agencyService.save(agency);

        return agencyMapper.toAgencyResponseDTO(agency);
    }

    @Transactional(readOnly = true)
    public List<AgencyResponseDTO> getAllAgencies() {

        List<Agency> agencies = agencyService.getAllAgencies();

        return agencies
                .stream()
                .map(agencyMapper::toAgencyResponseDTO)
                .toList();
    }

    @Transactional
    public ManagerResponseDTO registerManager(ManagerRegistrationDTO dto) {

        userValidator.checkCpf(dto.cpf());

        String encryptedPassword = passwordEncoder.encode(dto.password());

        Agency agency = agencyService.getAgencyByNumber(dto.agencyNumber());

        Manager manager = managerFactory.createManager(
                dto,
                encryptedPassword,
                agency
        );
        managerService.save(manager);

        return managerMapper.toManagerResponseDTO(manager);
    }

    @Transactional(readOnly = true)
    public List<ManagerResponseDTO> getAllManagers() {

        List<Manager> managers = managerService.getAllManagers();

        return managers
                .stream()
                .map(managerMapper::toManagerResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ManagerBasicViewDTO> getManagersByAgencyNumber(String agencyNumber) {

        agencyValidator.checkAgencyExists(agencyNumber);

        List<Manager> managers = managerService.getAllManagersByAgencyNumber(agencyNumber);

        return managers
                .stream()
                .map(managerMapper::toManagerBasicViewDTO)
                .toList();
    }
}