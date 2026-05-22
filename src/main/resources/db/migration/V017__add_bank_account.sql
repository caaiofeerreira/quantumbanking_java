CREATE TABLE tb_bank_account (id BIGINT AUTO_INCREMENT PRIMARY KEY, balance DECIMAL(19, 2) NOT NULL);

ALTER TABLE tb_bank
    ADD COLUMN bank_account_id BIGINT UNIQUE,
    ADD CONSTRAINT fk_bank_bank_account
        FOREIGN KEY (bank_account_id) REFERENCES tb_bank_account(id);