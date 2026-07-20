package com.quantumbanking.external;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantumbanking.modules.pixKey.dict.DictClient;
import com.quantumbanking.modules.pixKey.dict.DictEntry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SimulatedDictClient implements DictClient {

    private final ObjectMapper objectMapper;
    private Map<String, DictEntry> registry;

    @PostConstruct
    void loadRegistry() {
        try (InputStream is = getClass().getResourceAsStream("/dict/simulated-external-keys.json")) {
            List<DictEntry> entries = objectMapper.readValue(is, new TypeReference<>() {
            });
            this.registry = entries.stream()
                    .collect(Collectors.toMap(DictEntry::pixKey, Function.identity()));
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao carregar registro DICT simulado", e);
        }
    }

    @Override
    public Optional<DictEntry> lookup(String pixKey) {
        return Optional.ofNullable(registry.get(pixKey));
    }
}