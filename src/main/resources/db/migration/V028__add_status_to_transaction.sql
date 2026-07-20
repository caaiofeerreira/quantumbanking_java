ALTER TABLE tb_transaction
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED';

ALTER TABLE tb_transaction
    ADD CONSTRAINT chk_transaction_status
        CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED'));

ALTER TABLE tb_transaction
    ALTER COLUMN status DROP DEFAULT;