package com.quantumbanking.modules.bank.service;

import com.quantumbanking.infra.exception.ResourceNotFoundException;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.bank.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;

    public Bank getBankByCode(String compe) {
        return bankRepository.findByCompe(compe)
                .orElseThrow(() ->  new ResourceNotFoundException("Banco com código " + compe + " não encontrado."));
    }
}