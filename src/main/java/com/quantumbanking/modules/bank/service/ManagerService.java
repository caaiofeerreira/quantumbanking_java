package com.quantumbanking.modules.bank.service;

import com.quantumbanking.infra.exception.UserNotFoundException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.domain.manager.Manager;
import com.quantumbanking.modules.bank.dto.AgencyAccountManagementDTO;
import com.quantumbanking.modules.bank.dto.ManagerProfileResponseDTO;
import com.quantumbanking.modules.bank.dto.ManagerRegistrationDTO;
import com.quantumbanking.modules.bank.factory.ManagerFactory;
import com.quantumbanking.modules.bank.mapper.AgencyMapper;
import com.quantumbanking.modules.bank.mapper.ManagerMapper;
import com.quantumbanking.modules.bank.repository.ManagerRepository;
import com.quantumbanking.modules.loan.domain.LoanStatus;
import com.quantumbanking.modules.loan.dto.LoanApprovedResponseDTO;
import com.quantumbanking.modules.loan.dto.LoanManagerViewDTO;
import com.quantumbanking.modules.loan.mapper.LoanMapper;
import com.quantumbanking.modules.loan.service.LoanService;
import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.dto.NormalizedUserData;
import com.quantumbanking.modules.shared.dto.UpdateAddressRequestDTO;
import com.quantumbanking.modules.shared.service.validation.CepValidator;
import com.quantumbanking.modules.shared.service.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerFactory managerFactory;
    private final ManagerRepository managerRepository;

    private final AgencyService agencyService;
    private final AccountService accountService;
    private final LoanService loanService;

    private final AgencyMapper agencyMapper;
    private final ManagerMapper managerMapper;
    private final LoanMapper loanMapper;

    private final UserValidator userValidator;
    private final CepValidator cepValidator;

    private final PasswordEncoder passwordEncoder;

    public Manager getAuthenticatedUserManager(Long userId) {

        return managerRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Gerente não encontrado."));
    }

    public List<Manager> getAllManagers() {
        return managerRepository.findAll();
    }

    public List<Manager> getAllManagersByAgencyNumber(String agencyNumber) {
        return managerRepository.findAllByAgency_AgencyNumber(agencyNumber);
    }

    @Transactional
    public Manager createManager(ManagerRegistrationDTO dto) {

        userValidator.checkCpfNotRegistered(dto.cpf());
        userValidator.checkEmailNotRegistered(dto.email());

        NormalizedUserData data = new NormalizedUserData(
                dto.name(),
                userValidator.normalizeCpf(dto.cpf()),
                userValidator.normalizePhone(dto.phone()),
                userValidator.normalizeEmail(dto.email()),
                passwordEncoder.encode(dto.password()),
                dto.address().street(),
                dto.address().number(),
                dto.address().complement(),
                dto.address().neighborhood(),
                dto.address().city(),
                dto.address().state().toUpperCase(),
                cepValidator.normalizeCep(dto.address().zipCode())
        );

        Agency agency = agencyService.getAgencyByNumber(dto.agencyNumber());

        Manager manager = managerFactory.createManager(data, agency);
        managerRepository.save(manager);

        return manager;
    }

    @Transactional(readOnly = true)
    public ManagerProfileResponseDTO getProfile(Long userId) {

        Manager manager = getAuthenticatedUserManager(userId);
        return managerMapper.toProfileResponseDTO(manager);
    }

    @Transactional
    public void updatePhone(Long userId, String phone) {

        String normalizedPhone = userValidator.normalizePhone(phone);

        Manager manager = getAuthenticatedUserManager(userId);
        manager.updatePhone(normalizedPhone);
        managerRepository.save(manager);
    }

    @Transactional
    public void updateEmail(Long userId, String email) {

        String normalizedEmail = userValidator.normalizeEmail(email);

        Manager manager = getAuthenticatedUserManager(userId);
        manager.updateEmail(normalizedEmail);
        managerRepository.save(manager);
    }

    @Transactional
    public void updateAddress(Long userId, UpdateAddressRequestDTO requestDTO) {

        String normalizedCep = cepValidator.normalizeCep(requestDTO.zipCode());

        Manager manager = getAuthenticatedUserManager(userId);

        Address address = new Address(
                requestDTO.street(),
                requestDTO.number(),
                requestDTO.complement(),
                requestDTO.neighborhood(),
                requestDTO.city(),
                requestDTO.state().toUpperCase(),
                normalizedCep
        );

        manager.updateAddress(address);
        managerRepository.save(manager);
    }

    @Transactional(readOnly = true)
    public List<AgencyAccountManagementDTO> getAccountsByAgency(Long userId) {

        Manager manager = getAuthenticatedUserManager(userId);

        List<Account> accounts = accountService.getAccountsByAgencyId(manager.getAgency().getId());

        return accounts.stream()
                .map(agencyMapper::toAccountManagementDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LoanManagerViewDTO> getLoanRequestsByAgency(Long userId) {

        Manager manager = getAuthenticatedUserManager(userId);

        return loanService.getLoansByAgencyAndStatus(manager.getAgency().getId(), LoanStatus.REQUESTED)
                .stream()
                .map(loanMapper::toLoanManagerViewDTO)
                .toList();
    }

    @Transactional
    public LoanApprovedResponseDTO approveLoan(Long userId, UUID loanId) {

        Manager manager = getAuthenticatedUserManager(userId);
        return loanService.approveLoan(loanId, manager);
    }

    @Transactional
    public void rejectLoan(Long userId, UUID loanId) {

        Manager manager = getAuthenticatedUserManager(userId);
        loanService.rejectLoan(loanId, manager);
    }
}