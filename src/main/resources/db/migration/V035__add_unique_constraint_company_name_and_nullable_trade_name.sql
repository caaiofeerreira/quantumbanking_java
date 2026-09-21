ALTER TABLE tb_company
    ADD CONSTRAINT uk_company_name UNIQUE (company_name);

ALTER TABLE tb_company
    MODIFY COLUMN trade_name VARCHAR(255) NULL;