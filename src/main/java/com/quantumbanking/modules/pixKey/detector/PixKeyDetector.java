package com.quantumbanking.modules.pixKey.detector;

import com.quantumbanking.infra.exception.InvalidPixKeyTypeException;
import com.quantumbanking.modules.pixKey.domain.PixKeyType;
import com.quantumbanking.modules.shared.util.FormattingUtils;

public class PixKeyDetector {

    private PixKeyDetector() {
    }

    public record PixKeyDetectionResult(PixKeyType type, String normalizedKey) {}

    public static PixKeyDetectionResult checkAndDetectKey(String key) {

        if (FormattingUtils.isValidEmail(key)) {
            return new PixKeyDetectionResult(PixKeyType.EMAIL, FormattingUtils.normalizeEmail(key));
        }

        if (FormattingUtils.isValidCpf(key)) {
            return new PixKeyDetectionResult(PixKeyType.CPF, FormattingUtils.normalizeCpf(key));
        }

        if (FormattingUtils.isValidMobilePhone(key)) {
            return new PixKeyDetectionResult(PixKeyType.PHONE, FormattingUtils.normalizePhone(key));
        }

        throw new InvalidPixKeyTypeException("Formato de chave Pix inválido: " + key);
    }
}