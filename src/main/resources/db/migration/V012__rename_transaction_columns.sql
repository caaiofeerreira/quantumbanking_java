ALTER TABLE tb_transaction RENAME COLUMN account_origin_id TO origin_account_id;
ALTER TABLE tb_transaction RENAME COLUMN account_destiny_id TO destiny_account_id;

ALTER TABLE tb_transaction RENAME COLUMN destiny_account TO destiny_account_number;