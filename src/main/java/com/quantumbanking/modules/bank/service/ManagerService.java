package com.quantumbanking.modules.bank.service;

import com.quantumbanking.infra.exception.ValidateException;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.domain.manager.Manager;
import com.quantumbanking.modules.bank.dto.ManagerRegistrationDTO;
import com.quantumbanking.modules.bank.dto.ManagerResponseDTO;
import com.quantumbanking.modules.bank.mapper.ManagerMapper;
import com.quantumbanking.modules.bank.repository.AgencyRepository;
import com.quantumbanking.modules.bank.repository.ManagerRepository;
import com.quantumbanking.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final AgencyRepository agencyRepository;

    private final ManagerMapper managerMapper;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ManagerResponseDTO managerRegister(ManagerRegistrationDTO dto) {

        if (userRepository.existsByCpf(dto.cpf())) {
            throw new ValidateException("CPF já cadastrado.");
        }

        Agency agency = agencyRepository.findByAgencyNumber(dto.agencyNumber())
                .orElseThrow(() -> {
                    String agencies = String.join(", ", agencyRepository.findAllAgencyNumbersAndCity());
                    return new ValidateException("Agência não encontrada. Disponíveis: " + agencies);
                });

        String encryptedPassword = passwordEncoder.encode(dto.password());
        Manager manager = new Manager(dto, encryptedPassword, agency);
        managerRepository.save(manager);

        return managerMapper.toResponseDTO(manager);
    }


}
