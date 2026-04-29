package com.quantumbanking.modules.bank.service;

import com.quantumbanking.infra.exception.ManagerNotFoundException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.repository.AccountRepository;
import com.quantumbanking.modules.bank.domain.manager.Manager;
import com.quantumbanking.modules.bank.dto.AgencyAccountManagementDTO;
import com.quantumbanking.modules.bank.mapper.AgencyMapper;
import com.quantumbanking.modules.bank.repository.ManagerRepository;
import com.quantumbanking.modules.shared.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final AccountRepository accountRepository;

    private final AgencyMapper agencyMapper;

    @Transactional(readOnly = true)
    public List<AgencyAccountManagementDTO> listAgencyAccounts(User user) {

        Manager manager = managerRepository.findById(user.getId())
                .orElseThrow(() -> new ManagerNotFoundException(""));

        Long agencyId = manager.getAgency().getId();

        List<Account> accounts = accountRepository.findByAgencyId(agencyId);

        return accounts
                .stream()
                .map(agencyMapper::toAccountManagementDTO)
                .toList();
    }
}