CREATE TABLE tb_transaction_outbox (
                                       id                BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       transaction_id    BINARY(16)   NOT NULL,
                                       status            VARCHAR(20)  NOT NULL DEFAULT 'PENDING_PUBLISH',
                                       retry_count       INT          NOT NULL DEFAULT 0,
                                       stream_message_id VARCHAR(50)  NULL,
                                       error_message     VARCHAR(500) NULL,
                                       created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       published_at      DATETIME     NULL,

                                       CONSTRAINT fk_outbox_transaction FOREIGN KEY (transaction_id) REFERENCES tb_transaction(id),
                                       CONSTRAINT uq_outbox_transaction UNIQUE (transaction_id),
                                       CONSTRAINT chk_outbox_status CHECK (status IN ('PENDING_PUBLISH', 'PUBLISHED', 'FAILED'))
);

CREATE INDEX idx_outbox_status_created ON tb_transaction_outbox (status, created_at);