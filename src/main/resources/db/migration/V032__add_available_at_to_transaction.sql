SET @@session.time_zone = '-03:00';

ALTER TABLE tb_transaction
    MODIFY COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN available_at TIMESTAMP NULL;

SET @@session.time_zone = @@global.time_zone;

CREATE INDEX idx_transaction_pending_withdrawal
    ON tb_transaction (type, status, available_at);