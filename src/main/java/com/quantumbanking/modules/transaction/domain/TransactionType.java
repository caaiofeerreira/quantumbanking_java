package com.quantumbanking.modules.transaction.domain;

import java.util.Optional;

public enum TransactionType {

    DEPOSIT {
        @Override
        public String getDisplayName(boolean isOrigin) { return "Depósito"; }

        @Override
        public Optional<String> getFixedCounterpartName(boolean isOrigin) {
            return Optional.of("Origem Externa");
        }
    },

    WITHDRAWAL {
        @Override
        public String getDisplayName(boolean isOrigin) { return "Saque"; }

        @Override
        public Optional<String> getFixedCounterpartName(boolean isOrigin) {
            return Optional.of("Retirada em Espécie");
        }
    },

    INTERNAL_TRANSFER {
        @Override
        public String getDisplayName(boolean isOrigin) {
            return isOrigin ? "Transferência Enviada" : "Transferência Recebida";
        }
    },

    EXTERNAL_TRANSFER {
        @Override
        public String getDisplayName(boolean isOrigin) {
            return isOrigin ? "Transferência Enviada" : "Transferência Recebida";
        }
    },

    PIX {
        @Override
        public String getDisplayName(boolean isOrigin) {
            return isOrigin ? "Pix Enviado" : "Pix Recebido";
        }
    },

    LOAN {
        @Override
        public String getDisplayName(boolean isOrigin) {
            return isOrigin ? "Parcela de Empréstimo" : "Crédito de Empréstimo";
        }
    },

    FEE {
        @Override
        public String getDisplayName(boolean isOrigin) { return "Tarifa de Saque"; }
    };

    public abstract String getDisplayName(boolean isOrigin);

    public Optional<String> getFixedCounterpartName(boolean isOrigin) {
        return Optional.empty();
    }
}