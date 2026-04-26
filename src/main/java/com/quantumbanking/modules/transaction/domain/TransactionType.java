package com.quantumbanking.modules.transaction.domain;

public enum TransactionType {

    DEPOSIT {
        @Override
        public String getDisplayName(boolean isOrigin) { return "Depósito"; }
        @Override
        public String getCounterpartName(Transaction t, boolean isOrigin) { return "Depósito em Espécie"; }
    },
    WITHDRAWAL {
        @Override
        public String getDisplayName(boolean isOrigin) { return "Saque"; }
        @Override
        public String getCounterpartName(Transaction t, boolean isOrigin) { return "Caixa Eletrônico"; }
    },
    INTERNAL_TRANSFER {
        @Override
        public String getDisplayName(boolean isOrigin) {
            return isOrigin ? "Transferencia Enviada" : "Transferencia Recebida";
        }
    },
    EXTERNAL_TRANSFER {
        @Override
        public String getDisplayName(boolean isOrigin) {
            return isOrigin ? "Transferencia Enviada" : "Transferencia Recebida";
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
            return isOrigin ? "Parcela de Emprestimo" : "Crédito de Emprestimo";
        }
        @Override
        public String getCounterpartName(Transaction t, boolean isOrigin) { return "Quantum Banking"; }
    };

    public abstract String getDisplayName(boolean isOrigin);

    public String getCounterpartName(Transaction t, boolean isOrigin) {
        return isOrigin
                ? (t.getAccountDestiny() != null ? t.getAccountDestiny().getClient().getName() : t.getDestinyName())
                : (t.getAccountOrigin() != null ? t.getAccountOrigin().getClient().getName() : "Origem Externa");
    }
}