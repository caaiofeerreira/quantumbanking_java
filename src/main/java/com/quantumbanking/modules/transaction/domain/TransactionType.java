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
    },

    FEE {
        @Override
        public String getDisplayName(boolean isOrigin) { return "Tarifa de Saque"; }
    };

    public abstract String getDisplayName(boolean isOrigin);

    public String getCounterpartName(Transaction t, boolean isOrigin) {
        if (isOrigin) {
            if (t.getDestinationAccount() != null) return t.getDestinationAccount().getClient().getName();
            if (t.getDestinationBankName() != null) return t.getDestinationBankName();
            if (t.getDestinationName() != null) return t.getDestinationName();
            return "Destinatário não identificado";
        } else {
            if (t.getOriginAccount() != null) return t.getOriginAccount().getClient().getName();
            if (t.getOriginName() != null) return t.getOriginName();
            return "Origem Externa";
        }
    }
}