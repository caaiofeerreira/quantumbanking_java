package com.quantumbanking.modules.pixKey.resolver;

import com.quantumbanking.infra.exception.PixKeyNotFoundException;
import com.quantumbanking.modules.bank.domain.bank.BankRegistry;
import com.quantumbanking.modules.bank.service.BankRegistryService;
import com.quantumbanking.modules.pixKey.dict.DictClient;
import com.quantumbanking.modules.pixKey.dto.PixKeyResolution;
import com.quantumbanking.modules.pixKey.service.PixKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PixKeyResolver {

    private final PixKeyService pixKeyService;
    private final DictClient dictClient;
    private final BankRegistryService bankRegistryService;

    public PixKeyResolution resolveKey(String normalizedKey) {

        return pixKeyService.getPixKey(normalizedKey)
                .map(key -> new PixKeyResolution(false, key.getAccount(), null, null))
                .or(() -> dictClient.lookup(normalizedKey)
                        .map(entry -> {
                            BankRegistry bank = bankRegistryService.getByCompe(entry.bankCompe());
                            return new PixKeyResolution(true, null, entry, bank);
                        }))
                .orElseThrow(() -> new PixKeyNotFoundException("Chave Pix não encontrada: " + normalizedKey));
    }
}