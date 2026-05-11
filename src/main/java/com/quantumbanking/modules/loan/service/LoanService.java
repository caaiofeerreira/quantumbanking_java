package com.quantumbanking.modules.loan.service;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.loan.domain.Loan;
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

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;

    private final LoanMapper loanMapper;

    private final LoanCalculator loanCalculator;

    private final AccountService accountService;

    @Value("${loan.interest-rate}")
    private BigDecimal interestRate;

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