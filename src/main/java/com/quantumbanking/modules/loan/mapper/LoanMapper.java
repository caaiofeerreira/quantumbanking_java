package com.quantumbanking.modules.loan.mapper;

import com.quantumbanking.modules.loan.domain.Loan;
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
}
