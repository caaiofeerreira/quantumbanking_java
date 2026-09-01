package com.quantumbanking.modules.bank.service;

import com.quantumbanking.infra.exception.ResourceNotFoundException;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.bank.domain.bank.BankAccount;
import com.quantumbanking.modules.bank.repository.BankAccountRepository;
import com.quantumbanking.modules.bank.repository.BankRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final BankAccountRepository bankAccountRepository;

    private final EntityManager entityManager;

    @Value("${bank.id}")
    private Long BANK_ID;

    public Bank getBank() {
        return bankRepository.findById(BANK_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Banco não encontrado"));
    }

    public Bank getBankByCode(String compe) {
        return bankRepository.findByCompe(compe)
                .orElseThrow(() ->  new ResourceNotFoundException("Banco com código " + compe + " não encontrado."));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void creditFee(Long bankAccountId, BigDecimal amount) {

        BankAccount bankAccount = bankAccountRepository.findByIdWithLock(bankAccountId)
                .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada"));

        entityManager.refresh(bankAccount, LockModeType.PESSIMISTIC_WRITE);

        bankAccount.credit(amount);

        bankAccountRepository.save(bankAccount);
    }

    public void save(BankAccount bankAccount) {
        bankAccountRepository.save(bankAccount);
    }
}