ALTER TABLE tb_transaction RENAME COLUMN origin_bank_code TO origin_bank_compe;

ALTER TABLE tb_transaction RENAME COLUMN destiny_account_id TO destination_account_id;
ALTER TABLE tb_transaction RENAME COLUMN destiny_name TO destination_name;
ALTER TABLE tb_transaction RENAME COLUMN destiny_account_number TO destination_account_number;
ALTER TABLE tb_transaction RENAME COLUMN destiny_agency TO destination_agency;
ALTER TABLE tb_transaction RENAME COLUMN destiny_bank_code TO destination_bank_compe;
ALTER TABLE tb_transaction RENAME COLUMN destiny_document TO destination_document;

ALTER TABLE tb_transaction ADD COLUMN destination_bank_name VARCHAR(255);