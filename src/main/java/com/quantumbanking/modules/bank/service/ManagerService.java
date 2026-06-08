package com.quantumbanking.modules.bank.service;

import com.quantumbanking.infra.exception.UserNotFoundException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.bank.domain.manager.Manager;
import com.quantumbanking.modules.bank.dto.AgencyAccountManagementDTO;
import com.quantumbanking.modules.bank.dto.ManagerProfileResponseDTO;
import com.quantumbanking.modules.bank.mapper.AgencyMapper;
import com.quantumbanking.modules.bank.mapper.ManagerMapper;
import com.quantumbanking.modules.bank.repository.ManagerRepository;
import com.quantumbanking.modules.loan.domain.LoanStatus;
import com.quantumbanking.modules.loan.dto.LoanApprovedResponseDTO;
import com.quantumbanking.modules.loan.dto.LoanManagerViewDTO;
import com.quantumbanking.modules.loan.mapper.LoanMapper;
import com.quantumbanking.modules.loan.service.LoanService;
import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.shared.dto.UpdateAddressRequestDTO;
import com.quantumbanking.modules.shared.service.validation.CepValidator;
import com.quantumbanking.modules.shared.service.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final AccountService accountService;
    private final LoanService loanService;

    private final AgencyMapper agencyMapper;
    private final ManagerMapper managerMapper;
    private final LoanMapper loanMapper;

    private final UserValidator userValidator;
    private final CepValidator cepValidator;

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

    public void save(Manager manager) {
        managerRepository.save(manager);
    }

    @Transactional(readOnly = true)
    public List<AgencyAccountManagementDTO> getAccountsByAgency(User user) {

        Manager manager = getAuthenticatedUserManager(user.getId());

        List<Account> accounts = accountService.getAccountsByAgencyId(manager.getAgency().getId());

        return accounts.stream()
                .map(agencyMapper::toAccountManagementDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LoanManagerViewDTO> getLoanRequestsByAgency(User user) {

        Manager manager = getAuthenticatedUserManager(user.getId());

        return loanService.getLoansByAgencyAndStatus(manager.getAgency().getId(), LoanStatus.REQUESTED)
                .stream()
                .map(loanMapper::toLoanManagerViewDTO)
                .toList();
    }

    @Transactional
    public LoanApprovedResponseDTO approveLoan(User user, UUID loanId) {

        Manager manager = getAuthenticatedUserManager(user.getId());
        return loanService.approveLoan(loanId, manager);
    }

    @Transactional
    public void rejectLoan(User user, UUID loanId) {

        Manager manager = getAuthenticatedUserManager(user.getId());
        loanService.rejectLoan(loanId, manager);
    }

    @Transactional(readOnly = true)
    public ManagerProfileResponseDTO getProfile(User user) {

        Manager manager = getAuthenticatedUserManager(user.getId());
        return managerMapper.toProfileResponseDTO(manager);
    }

    @Transactional
    public void updatePhone(User user, String phone) {

        String normalizedPhone = userValidator.normalizePhone(phone);

        Manager manager = getAuthenticatedUserManager(user.getId());
        manager.updatePhone(normalizedPhone);
        managerRepository.save(manager);
    }

    @Transactional
    public void updateEmail(User user, String email) {

        String normalizedEmail = userValidator.normalizeEmail(email);

        Manager manager = getAuthenticatedUserManager(user.getId());
        manager.updateEmail(normalizedEmail);
        managerRepository.save(manager);
    }

    @Transactional
    public void updateAddress(User user, UpdateAddressRequestDTO requestDTO) {

        String normalizedCep = cepValidator.normalizeCep(requestDTO.zipCode());

        Manager manager = getAuthenticatedUserManager(user.getId());

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
}