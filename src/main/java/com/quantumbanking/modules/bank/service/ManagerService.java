package com.quantumbanking.modules.bank.service;

import com.quantumbanking.modules.account.repository.AccountRepository;
import com.quantumbanking.modules.bank.domain.manager.Manager;
import com.quantumbanking.modules.bank.dto.AgencyAccountManagementDTO;
import com.quantumbanking.modules.bank.mapper.AgencyMapper;
import com.quantumbanking.modules.loan.domain.LoanStatus;
import com.quantumbanking.modules.loan.dto.LoanResponseDTO;
import com.quantumbanking.modules.loan.mapper.LoanMapper;
import com.quantumbanking.modules.loan.repository.LoanRepository;
import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.shared.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final AccountRepository accountRepository;
    private final LoanRepository loanRepository;

    private final AgencyMapper agencyMapper;
    private final LoanMapper loanMapper;

    private final UserService userService;

    @Transactional(readOnly = true)
    public List<AgencyAccountManagementDTO> getAccountsByAgency(User user) {

        Manager manager = userService.getAuthenticatedUserManager(user.getId());

        return accountRepository.findByAgencyId(manager.getAgency().getId())
                .stream()
                .map(agencyMapper::toAccountManagementDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LoanResponseDTO> getLoanRequestsByAgency(User user) {

        Manager manager = userService.getAuthenticatedUserManager(user.getId());

        return loanRepository.findByAgencyIdAndStatus(manager.getAgency().getId(), LoanStatus.REQUESTED)
                .stream()
                .map(loanMapper::toLoanResponseDTO)
                .toList();
    }
}