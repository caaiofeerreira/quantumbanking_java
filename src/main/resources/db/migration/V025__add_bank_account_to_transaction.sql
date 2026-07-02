ALTER TABLE tb_transaction
    ADD COLUMN bank_account_id BIGINT NULL,
    ADD CONSTRAINT fk_transaction_bank_account
        FOREIGN KEY (bank_account_id) REFERENCES tb_bank_account (id);