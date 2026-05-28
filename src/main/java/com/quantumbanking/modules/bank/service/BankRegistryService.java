package com.quantumbanking.modules.bank.service;

import com.quantumbanking.infra.exception.ResourceNotFoundException;
import com.quantumbanking.modules.bank.domain.bank.BankRegistry;
import com.quantumbanking.modules.bank.repository.BankRegistryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankRegistryService {

    private final BankRegistryRepository bankRegistryRepository;

    public BankRegistry getByCompe(String compe) {
        return bankRegistryRepository.findByCompe(compe)
                .orElseThrow(() ->  new ResourceNotFoundException("Banco com código " + compe + " não encontrado."));
    }
}