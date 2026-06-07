package com.quantumbanking.modules.shared.service.validation;

import com.quantumbanking.infra.exception.CpfAlreadyRegisteredException;
import com.quantumbanking.infra.exception.InvalidCepException;
import com.quantumbanking.infra.exception.InvalidEmailException;
import com.quantumbanking.infra.exception.InvalidPhoneException;
import com.quantumbanking.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\(?\\d{2}\\)?[\\s-]?9?\\d{4}[-\\s]?\\d{4}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern CEP_PATTERN = Pattern.compile("\\d{5}-?\\d{3}");
    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    public String normalizePhone(String phone) {

        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new InvalidPhoneException("Telefone inválido: " + phone);
        }

        String digits = phone.replaceAll("\\D", "");

        if (digits.length() == 10) {
            digits = digits.substring(0, 2) + "9" + digits.substring(2);
        }

        return "+55" + digits;
    }

    public String normalizeEmail(String email) {

        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new InvalidEmailException("Formato de e-mail inválido.");
        }

        return email.trim().toLowerCase();
    }

    public void checkCpf(String cpf) {

        if (userRepository.existsByCpf(cpf)) {
            throw new CpfAlreadyRegisteredException("Este CPF já está vinculado a outro usuário.");
        }
    }

    public void checkCep(String cep) {

        String normalizedCep = cep.replace("-", "");

        try {
            Map<String, Object> response = restTemplate.getForObject(
                    VIA_CEP_URL, Map.class, normalizedCep
            );

            if (response == null || response.containsKey("erro")) {
                throw new InvalidCepException("CEP não encontrado: " + cep);
            }

        } catch (RestClientException e) {
            throw new InvalidCepException("Erro ao consultar CEP: " + cep);
        }
    }

    public String normalizeCep(String cep) {

        if (!CEP_PATTERN.matcher(cep.trim()).matches()) {
            throw new InvalidCepException("CEP inválido: " + cep);
        }

        String digits = cep.replaceAll("\\D", "");
        return digits.substring(0,5) + "-" + digits.substring(5);
    }
}