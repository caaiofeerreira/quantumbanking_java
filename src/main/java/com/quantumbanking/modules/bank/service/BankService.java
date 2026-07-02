package com.quantumbanking.modules.bank.service;

import com.quantumbanking.infra.exception.ResourceNotFoundException;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.bank.domain.bank.BankAccount;
import com.quantumbanking.modules.bank.repository.BankAccountRepository;
import com.quantumbanking.modules.bank.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankService {

    private static final Long BANK_ID = 1L;

    private final BankRepository bankRepository;
    private final BankAccountRepository bankAccountRepository;

    public Bank getBank() {
        return bankRepository.findById(BANK_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Banco não encontrado"));
    }

    public Bank getBankByCode(String compe) {
        return bankRepository.findByCompe(compe)
                .orElseThrow(() ->  new ResourceNotFoundException("Banco com código " + compe + " não encontrado."));
    }

    public void save(BankAccount bankAccount) {
        bankAccountRepository.save(bankAccount);
    }
}