package com.quantumbanking.modules.manager.service;

import com.quantumbanking.infra.exception.UserNotFoundException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.bank.service.AgencyService;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.manager.domain.Manager;
import com.quantumbanking.modules.bank.dto.AgencyAccountManagementDTO;
import com.quantumbanking.modules.manager.dto.ManagerProfileResponseDTO;
import com.quantumbanking.modules.manager.dto.ManagerRegistrationDTO;
import com.quantumbanking.modules.manager.factory.ManagerFactory;
import com.quantumbanking.modules.bank.mapper.AgencyMapper;
import com.quantumbanking.modules.manager.mapper.ManagerMapper;
import com.quantumbanking.modules.manager.repository.ManagerRepository;
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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public Long getAuthenticatedUserAgencyId(Long userId) {
        return managerRepository.findAgencyIdByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Gerente não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<Manager> getAllManagers() {
        return managerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Manager> getAllManagersByAgencyNumber(String agencyNumber) {

        Long agencyId = agencyService.getAgencyIdByNumber(agencyNumber);
        return managerRepository.findAllByAgencyId(agencyId);
    }

    @Transactional
    public Manager createManager(ManagerRegistrationDTO dto) {

        userValidator.checkCpfNotRegistered(dto.cpf());
        userValidator.checkEmailNotRegistered(dto.email());
        userValidator.checkPhoneNotRegistered(dto.phone());

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

        Long agencyId = agencyService.getAgencyIdByNumber(dto.agencyNumber());

        Manager manager = managerFactory.createManager(data, agencyId);
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

        Long agencyId = getAuthenticatedUserAgencyId(userId);

        List<Account> accounts = accountService.getAccountsByAgencyId(agencyId);

        Map<Client, List<Account>> accountsByClient = accounts.stream()
                .collect(Collectors.groupingBy(Account::getClient));

        return accountsByClient
                .entrySet()
                .stream()
                .map(entry -> agencyMapper.toAccountManagementDTO(entry.getKey(), entry.getValue()))
                .toList();
    }

    public List<LoanManagerViewDTO> getLoanRequestsByAgency(Long userId) {

        Long agencyId = getAuthenticatedUserAgencyId(userId);

        return loanService.getLoansByAgencyAndStatus(agencyId, LoanStatus.REQUESTED)
                .stream()
                .map(loanMapper::toLoanManagerViewDTO)
                .toList();
    }

    public LoanApprovedResponseDTO approveLoan(Long userId, UUID loanId) {

        Manager manager = getAuthenticatedUserManager(userId);
        return loanService.approveLoan(loanId, manager);
    }

    public void rejectLoan(Long userId, UUID loanId) {

        Manager manager = getAuthenticatedUserManager(userId);
        loanService.rejectLoan(loanId, manager);
    }
}