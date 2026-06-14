ALTER TABLE `tb_account`
DROP INDEX `client_id`,
    ADD CONSTRAINT `uq_client_account_type` UNIQUE (`client_id`, `type`);