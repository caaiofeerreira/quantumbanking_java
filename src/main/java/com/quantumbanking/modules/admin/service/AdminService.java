package com.quantumbanking.modules.admin.service;

import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.bank.domain.manager.Manager;
import com.quantumbanking.modules.bank.dto.*;
import com.quantumbanking.modules.bank.mapper.AgencyMapper;
import com.quantumbanking.modules.bank.mapper.ManagerMapper;
import com.quantumbanking.modules.bank.service.AgencyService;
import com.quantumbanking.modules.bank.service.BankService;
import com.quantumbanking.modules.bank.service.ManagerService;
import com.quantumbanking.modules.bank.service.validation.AgencyValidator;
import lombok.RequiredArgsConstructor;
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

    private final AgencyValidator agencyValidator;

    public AgencyResponseDTO registerAgency(AgencyRegistrationDTO dto) {

        Bank bank = bankService.getBankByCode(dto.compe());
        Agency agency = agencyService.createAgency(dto, bank);

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

    public ManagerResponseDTO registerManager(ManagerRegistrationDTO dto) {

        Manager manager = managerService.createManager(dto);
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