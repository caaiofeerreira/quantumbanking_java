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
                loan.getTotalAmount(),
                loan.getInstallmentAmount(),
                loan.getInterestRate(),
                loan.getInstallments(),
                loan.getDescription(),
                loan.getStatus(),
                loan.getCreatedAt()
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

    public LoanManagerViewDTO toLoanManagerViewDTO(Loan loan) {
        return new LoanManagerViewDTO(
                loan.getAccount().getClient().getName(),
                loan.getAccount().getAccountNumber(),
                loan.getAccount().getBalance(),
                loan.getAccount().getType(),
                toLoanResponseDTO(loan)
        );
    }
}