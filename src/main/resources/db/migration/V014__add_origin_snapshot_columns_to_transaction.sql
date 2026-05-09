ALTER TABLE tb_transaction
    ADD COLUMN origin_account_number VARCHAR(14),
    ADD COLUMN origin_agency VARCHAR(5),
    ADD COLUMN origin_document VARCHAR(100),
    ADD COLUMN origin_bank_code VARCHAR(5);