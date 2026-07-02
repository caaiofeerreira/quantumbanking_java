ALTER TABLE tb_transaction ADD COLUMN loan_id BINARY(16) NULL,
    ADD CONSTRAINT fk_transaction_loan
    FOREIGN KEY (loan_id) REFERENCES tb_loan (id);