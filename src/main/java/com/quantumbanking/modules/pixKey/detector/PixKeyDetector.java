package com.quantumbanking.modules.pixKey.detector;

import com.quantumbanking.infra.exception.InvalidPixKeyTypeException;
import com.quantumbanking.modules.pixKey.domain.PixKeyType;
import com.quantumbanking.modules.pixKey.dto.PixKeyDetectionResult;
import com.quantumbanking.modules.shared.util.FormattingUtils;

public class PixKeyDetector {

    private PixKeyDetector() {
    }

    public static PixKeyDetectionResult checkAndDetectKey(String key) {

        if (FormattingUtils.isValidEmail(key)) {
            return new PixKeyDetectionResult(PixKeyType.EMAIL, FormattingUtils.normalizeEmail(key));
        }

        if (FormattingUtils.isValidCpf(key)) {
            return new PixKeyDetectionResult(PixKeyType.CPF, FormattingUtils.normalizeCpf(key));
        }

        if (FormattingUtils.isValidCnpj(key)) {
            return new PixKeyDetectionResult(PixKeyType.CNPJ, FormattingUtils.normalizeCnpj(key));
        }

        if (FormattingUtils.isValidMobilePhone(key)) {
            return new PixKeyDetectionResult(PixKeyType.PHONE, FormattingUtils.normalizePhone(key));
        }

        throw new InvalidPixKeyTypeException("Formato de chave Pix inválido: " + key);
    }
}