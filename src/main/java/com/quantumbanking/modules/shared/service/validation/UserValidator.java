package com.quantumbanking.modules.shared.service.validation;

import com.quantumbanking.infra.exception.*;
import com.quantumbanking.modules.shared.repository.UserRepository;
import com.quantumbanking.modules.shared.util.FormattingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public String normalizePhone(String phone) {

        if (!FormattingUtils.isValidMobilePhone(phone)) {
            throw new InvalidPhoneException("Telefone inválido: " + phone);
        }

        return FormattingUtils.normalizePhone(phone);
    }

    public String normalizeEmail(String email) {

        if (!FormattingUtils.isValidEmail(email)) {
            throw new InvalidEmailException("Formato de e-mail inválido.");
        }
        return FormattingUtils.normalizeEmail(email);
    }

    public String normalizeCpf(String cpf) {

        if (!FormattingUtils.isValidCpf(cpf)) {
            throw new InvalidCpfException("CPF inválido: " + cpf);
        }
        return FormattingUtils.normalizeCpf(cpf);
    }

    public void checkEmailNotRegistered(String email) {
        String normalized = normalizeEmail(email);
        if (userRepository.existsByEmail(normalized)) {
            throw new EmailAlreadyRegisteredException("Este e-mail já está vinculado a outro usuário.");
        }
    }

    public void checkCpfNotRegistered(String cpf) {
        String normalized = normalizeCpf(cpf);
        if (userRepository.existsByCpf(normalized)) {
            throw new CpfAlreadyRegisteredException("Este CPF já está vinculado a outro usuário.");
        }
    }

    public void checkPhoneNotRegistered(String phone) {
        String normalized = normalizePhone(phone);
        if (userRepository.existsByPhone(normalized)) {
            throw new PhoneAlreadyExistsException("Este telefone já está vinculado a outro usuário.");
        }
    }
}