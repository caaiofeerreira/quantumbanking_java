package com.quantumbanking.modules.admin.service;

import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.manager.domain.Manager;
import com.quantumbanking.modules.bank.dto.*;
import com.quantumbanking.modules.bank.mapper.AgencyMapper;
import com.quantumbanking.modules.manager.mapper.ManagerMapper;
import com.quantumbanking.modules.bank.service.AgencyService;
import com.quantumbanking.modules.bank.service.BankService;
import com.quantumbanking.modules.manager.service.ManagerService;
import com.quantumbanking.modules.manager.dto.ManagerBasicViewDTO;
import com.quantumbanking.modules.manager.dto.ManagerRegistrationDTO;
import com.quantumbanking.modules.manager.dto.ManagerResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AgencyService agencyService;
    private final BankService bankService;
    private final ManagerService managerService;
    private final AgencyMapper agencyMapper;
    private final ManagerMapper managerMapper;

    @Transactional
    public AgencyResponseDTO registerAgency(AgencyRegistrationDTO dto) {

        Bank bank = bankService.getBankByCode(dto.compe());
        Agency agency = agencyService.createAgency(dto, bank);

        return agencyMapper.toAgencyResponseDTO(agency);
    }

    @Transactional
    public ManagerResponseDTO registerManager(ManagerRegistrationDTO dto) {

        Manager manager = managerService.createManager(dto);
        Agency agency = agencyService.getAgencyById(manager.getAgencyId());
        return managerMapper.toManagerResponseDTO(manager, agency);
    }

    public List<AgencyResponseDTO> getAllAgencies() {

        List<Agency> agencies = agencyService.getAllAgencies();

        return agencies
                .stream()
                .map(agencyMapper::toAgencyResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ManagerResponseDTO> getAllManagers() {

        List<Manager> managers = managerService.getAllManagers();

        List<Long> agencyIds = managers.stream()
                .map(Manager::getAgencyId)
                .distinct()
                .toList();

        List<Agency> agencies = agencyService.getAgenciesByIds(agencyIds);

        Map<Long, Agency> agencyById = agencies.stream()
                .collect(Collectors.toMap(Agency::getId, Function.identity()));

        return managers.stream()
                .map(manager -> managerMapper.toManagerResponseDTO(manager, agencyById.get(manager.getAgencyId())))
                .toList();
    }

    public List<ManagerBasicViewDTO> getManagersByAgencyNumber(String agencyNumber) {

        List<Manager> managers = managerService.getAllManagersByAgencyNumber(agencyNumber);

        return managers.stream()
                .map(managerMapper::toManagerBasicViewDTO)
                .toList();
    }
}