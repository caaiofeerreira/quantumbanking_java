package com.quantumbanking.modules.shared.service.validation;

import com.quantumbanking.infra.exception.CpfAlreadyRegisteredException;
import com.quantumbanking.infra.exception.InvalidPhoneException;
import com.quantumbanking.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\(?\\d{2}\\)?[\\s-]?9?\\d{4}[-\\s]?\\d{4}$");

    public String checkPhone(String phone) {

        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new InvalidPhoneException("Telefone inválido: " + phone);
        }

        String digits = phone.replaceAll("\\D", "");

        if (digits.length() == 10) {
            digits = digits.substring(0, 2) + "9" + digits.substring(2);
        }

        return "+55" + digits;
    }

    public void validateCpfNotRegistered(String cpf) {

        if (userRepository.existsByCpf(cpf)) {
            throw new CpfAlreadyRegisteredException("Este CPF já está vinculado a outro usuário.");
        }
    }
}