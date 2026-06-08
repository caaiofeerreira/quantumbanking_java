package com.quantumbanking.modules.shared.service.validation;

import com.quantumbanking.infra.exception.InvalidCepException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class CepValidator {

    private final RestTemplate restTemplate;

    private static final Pattern CEP_PATTERN = Pattern.compile("\\d{5}-?\\d{3}");
    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    public String normalizeCep(String cep) {

        String digits = cep.replaceAll("\\D", "");

        if (!CEP_PATTERN.matcher(digits).matches()) {
            throw new InvalidCepException("CEP inválido: " + cep);
        }

        try {
            Map<String, Object> response = restTemplate.getForObject(
                    VIA_CEP_URL, Map.class, digits
            );

            if (response == null || response.containsKey("erro")) {
                throw new InvalidCepException("CEP não encontrado: " + cep);
            }

        } catch (RestClientException e) {
            throw new InvalidCepException("Erro ao consultar CEP: " + cep);
        }

        return digits.substring(0, 5) + "-" + digits.substring(5);
    }
}