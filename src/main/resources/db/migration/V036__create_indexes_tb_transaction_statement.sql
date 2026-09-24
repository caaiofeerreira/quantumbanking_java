CREATE INDEX idx_tb_transaction_origin_date
    ON tb_transaction (origin_account_id, created_at);

CREATE INDEX idx_tb_transaction_destination_date
    ON tb_transaction (destination_account_id, created_at);