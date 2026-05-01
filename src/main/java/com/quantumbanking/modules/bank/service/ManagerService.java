package com.quantumbanking.modules.bank.service;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.repository.AccountRepository;
import com.quantumbanking.modules.bank.domain.manager.Manager;
import com.quantumbanking.modules.bank.dto.AgencyAccountManagementDTO;
import com.quantumbanking.modules.bank.mapper.AgencyMapper;
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

    private final AgencyMapper agencyMapper;

    private final UserService userService;

    @Transactional(readOnly = true)
    public List<AgencyAccountManagementDTO> listAgencyAccounts(User user) {

        Manager manager = userService.getAuthenticatedUserManager(user.getId());

        Long agencyId = manager.getAgency().getId();

        List<Account> accounts = accountRepository.findByAgencyId(agencyId);

        return accounts
                .stream()
                .map(agencyMapper::toAccountManagementDTO)
                .toList();
    }
}