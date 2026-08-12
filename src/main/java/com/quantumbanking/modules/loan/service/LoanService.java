package com.quantumbanking.modules.loan.service;

import com.quantumbanking.infra.exception.LoanStatusException;
import com.quantumbanking.infra.exception.UnauthorizedAccessException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.manager.domain.Manager;
import com.quantumbanking.modules.loan.domain.Loan;
import com.quantumbanking.modules.loan.domain.LoanStatus;
import com.quantumbanking.modules.loan.dto.LoanApprovedResponseDTO;
import com.quantumbanking.modules.loan.dto.LoanRequestDTO;
import com.quantumbanking.modules.loan.dto.LoanResponseDTO;
import com.quantumbanking.modules.loan.mapper.LoanMapper;
import com.quantumbanking.modules.loan.repository.LoanRepository;
import com.quantumbanking.modules.loan.service.validation.LoanValidator;
import com.quantumbanking.modules.transaction.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final AccountService accountService;
    private final TransactionService transactionService;

    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
    private final LoanCalculator loanCalculator;
    private final LoanValidator loanValidator;

    @Value("${loan.interest-rate}")
    private BigDecimal interestRate;

    public List<Loan> getLoansByAgencyAndStatus(Long agencyId, LoanStatus status) {
        return loanRepository.findByAgencyIdAndStatus(agencyId, status);
    }

    private Loan getLoanByIdAndAgency(UUID loanId, Manager manager) {

        Loan loan = loanRepository.findByIdWithDetails(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Empréstimo não encontrado."));

        if (loan.getStatus() != LoanStatus.REQUESTED) {
            throw new LoanStatusException("O empréstimo não está em status de SOLICITADO.");
        }

        if (!loan.getAccount().getAgency().getId().equals(manager.getAgencyId())) {
            throw new UnauthorizedAccessException("O empréstimo não pertence à sua agência.");
        }

        return loan;
    }

    // AÇÕES DO CLIENTE

    @Transactional
    public LoanResponseDTO processLoan(Long userId, String accountNumber, LoanRequestDTO requestDTO) {

        Account account = accountService.getAuthenticatedUserAccount(userId, accountNumber);

        loanValidator.validateLoan(account);

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
                requestDTO.description(),
                installmentAmount,
                totalAmount
        );

        loanRepository.save(loan);

        return loanMapper.toLoanResponseDTO(loan);
    }

    @Transactional(readOnly = true)
    public List<LoanResponseDTO> getLoansByAccount(Long userId, String accountNumber) {

        Account account = accountService.getAuthenticatedUserAccount(userId, accountNumber);

        List<Loan> loans = loanRepository.findByAccountId(account.getId());
        return loans
                .stream()
                .map(loanMapper::toLoanResponseDTO)
                .toList();
    }

    // AÇÕES DO GERENTE

    @Transactional
    public LoanApprovedResponseDTO approveLoan(UUID loanId, Manager manager) {

        Loan loan = getLoanByIdAndAgency(loanId, manager);
        loan.approveLoan(manager);
        transactionService.executeLoan(loan);

        return loanMapper.toLoanApprovedDTO(loan);
    }

    @Transactional
    public void rejectLoan(UUID loanId, Manager manager) {

        Loan loan = getLoanByIdAndAgency(loanId, manager);
        loan.rejectLoan(manager);
    }
}