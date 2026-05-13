package com.quantumbanking.modules.loan.service;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.loan.domain.Loan;
import com.quantumbanking.modules.loan.domain.LoanStatus;
import com.quantumbanking.modules.loan.dto.LoanRequestDTO;
import com.quantumbanking.modules.loan.dto.LoanResponseDTO;
import com.quantumbanking.modules.loan.mapper.LoanMapper;
import com.quantumbanking.modules.loan.repository.LoanRepository;
import com.quantumbanking.modules.shared.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final AccountService accountService;

    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
    private final LoanCalculator loanCalculator;

    @Value("${loan.interest-rate}")
    private BigDecimal interestRate;

    public List<Loan> getLoansByAgencyAndStatus(Long agencyId, LoanStatus status) {
        return loanRepository.findByAgencyIdAndStatus(agencyId, status);
    }

    @Transactional
    public LoanResponseDTO processLoan(User user, LoanRequestDTO requestDTO) {

        Account account = accountService.getAuthenticatedUserAccount(user.getId());

        BigDecimal installmentAmount = loanCalculator.calculateInstallmentAmount(
                requestDTO.amount(),
                interestRate,
                requestDTO.installments()
        );

        BigDecimal totalAmount = loanCalculator.calculateTotal(
                installmentAmount,
                requestDTO.installments()
        );

        Loan loan = new Loan(
                account,
                requestDTO.amount(),
                interestRate,
                requestDTO.installments(),
                requestDTO.description()
        );

        loan.setInstallmentAmount(installmentAmount);
        loan.setTotalAmount(totalAmount);

        loanRepository.save(loan);

        return loanMapper.toLoanResponseDTO(loan);
    }
}