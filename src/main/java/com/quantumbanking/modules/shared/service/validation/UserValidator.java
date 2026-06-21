package com.quantumbanking.modules.shared.service.validation;

import com.quantumbanking.infra.exception.*;
import com.quantumbanking.modules.shared.repository.UserRepository;
import com.quantumbanking.modules.shared.util.FormattingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\(?\\d{2}\\)?[\\s-]?9?\\d{4}[-\\s]?\\d{4}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$");

    public String normalizeCpf(String cpf) {
        return FormattingUtils.normalizeCpf(cpf);
    }

    public String normalizeEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new InvalidEmailException("E-mail não pode ser nulo.");
        }

        String cleanedEmail = email.trim();
        if (!EMAIL_PATTERN.matcher(cleanedEmail).matches()) {
            throw new InvalidEmailException("Formato de e-mail inválido.");
        }

        return FormattingUtils.normalizeEmail(cleanedEmail);
    }

    public String normalizePhone(String phone) {

        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new InvalidPhoneException("Telefone inválido: " + phone);
        }

        return FormattingUtils.normalizePhone(phone);
    }

    public void checkCpfNotRegistered(String cpf) {

        String normalized = normalizeCpf(cpf);

        if (userRepository.existsByCpf(normalized)) {
            throw new CpfAlreadyRegisteredException("Este CPF já está vinculado a outro usuário.");
        }
    }

    public void checkEmailNotRegistered(String email) {

        String normalized = normalizeEmail(email);

        if (userRepository.existsByEmail(normalized)) {
            throw new EmailAlreadyRegisteredException("Este e-mail já está vinculado a outro usuário.");
        }
    }

    public void checkPhoneNotRegistered(String phone) {

        String normalized = normalizePhone(phone);

        if (userRepository.existsByPhone(normalized)) {
            throw new PhoneAlreadyExistsException("Este telefone já está vinculado a outro usuário.");
        }
    }
}