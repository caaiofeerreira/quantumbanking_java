package com.quantumbanking.modules.transaction.domain;

public enum TransactionType {

    DEPOSIT {
        @Override
        public String getDisplayName(boolean isOrigin) { return "Depósito"; }

        @Override
        public String getCounterpartName(Transaction t, boolean isOrigin) {
            return "Origem Externa";
        }
    },

    WITHDRAWAL {
        @Override
        public String getDisplayName(boolean isOrigin) { return "Saque"; }

        @Override
        public String getCounterpartName(Transaction t, boolean isOrigin) {
            return "Retirada em Espécie";
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

        @Override
        public String getCounterpartName(Transaction t, boolean isOrigin) {
            return "Quantum Banking";
        }
    };

    public abstract String getDisplayName(boolean isOrigin);

    public String getCounterpartName(Transaction t, boolean isOrigin) {
        if (isOrigin) {
            return (t.getDestinationAccount() != null)
                    ? t.getDestinationAccount().getClient().getName()
                    : (t.getDestinationName() != null ? t.getDestinationName() : "Destinatário não identificado");
        } else {
            return (t.getOriginAccount() != null)
                    ? t.getOriginAccount().getClient().getName()
                    : (t.getOriginName() != null ? t.getOriginName() : "Origem Externa");
        }
    }
}