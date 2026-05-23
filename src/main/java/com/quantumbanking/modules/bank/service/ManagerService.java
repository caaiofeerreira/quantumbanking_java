package com.quantumbanking.modules.bank.service;

import com.quantumbanking.infra.exception.UserNotFoundException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.bank.domain.manager.Manager;
import com.quantumbanking.modules.bank.dto.AgencyAccountManagementDTO;
import com.quantumbanking.modules.bank.mapper.AgencyMapper;
import com.quantumbanking.modules.bank.repository.ManagerRepository;
import com.quantumbanking.modules.loan.domain.LoanStatus;
import com.quantumbanking.modules.loan.dto.LoanApprovedResponseDTO;
import com.quantumbanking.modules.loan.dto.LoanResponseDTO;
import com.quantumbanking.modules.loan.mapper.LoanMapper;
import com.quantumbanking.modules.loan.service.LoanService;
import com.quantumbanking.modules.shared.domain.user.User;
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
    private final LoanMapper loanMapper;

    public Manager getAuthenticatedUserManager(Long userId) {

        return managerRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Gerente não encontrado."));
    }

    public List<Manager> getAllManagers() {
        return managerRepository.findAll();
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
    public List<LoanResponseDTO> getLoanRequestsByAgency(User user) {

        Manager manager = getAuthenticatedUserManager(user.getId());

        return loanService.getLoansByAgencyAndStatus(manager.getAgency().getId(), LoanStatus.REQUESTED)
                .stream()
                .map(loanMapper::toLoanResponseDTO)
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
}