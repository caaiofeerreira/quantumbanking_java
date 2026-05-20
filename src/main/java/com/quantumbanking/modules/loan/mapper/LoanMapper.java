package com.quantumbanking.modules.loan.mapper;

import com.quantumbanking.modules.loan.domain.Loan;
import com.quantumbanking.modules.loan.dto.LoanApprovedResponseDTO;
import com.quantumbanking.modules.loan.dto.LoanManagerViewDTO;
import com.quantumbanking.modules.loan.dto.LoanResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {

    public LoanResponseDTO toLoanResponseDTO(Loan loan) {
        return new LoanResponseDTO(
                loan.getId(),
                loan.getAmount(),
                loan.getInterestRate(),
                loan.getInstallments(),
                loan.getDescription(),
                loan.getCreatedAt(),
                loan.getStatus()
        );
    }

    public LoanApprovedResponseDTO toLoanApproved(Loan loan) {
        return new LoanApprovedResponseDTO(
                loan.getId(),
                loan.getAmount(),
                loan.getInterestRate(),
                loan.getInstallments(),
                loan.getTotalAmount(),
                loan.getInstallmentAmount(),
                loan.getStartDate(),
                loan.getEndDate(),
                loan.getStatus(),
                loan.getManager().getName()
        );
    }

    public LoanManagerViewDTO toLoanManagerView(Loan loan) {
        return new LoanManagerViewDTO(
                loan.getId(),
                loan.getAmount(),
                loan.getInterestRate(),
                loan.getInstallments(),
                loan.getDescription(),
                loan.getCreatedAt(),
                loan.getStatus(),
                loan.getAccount().getClient().getName(),
                loan.getAccount().getClient().getCpf(),
                loan.getAccount().getAccountNumber(),
                loan.getAccount().getBalance()
        );
    }
}