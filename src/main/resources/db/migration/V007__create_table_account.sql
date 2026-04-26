CREATE TABLE `tb_account` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `number` varchar(10) DEFAULT NULL,
                              `type` varchar(20) NOT NULL,
                              `balance` decimal(19,2) DEFAULT '0.00',
                              `status` varchar(20) NOT NULL,
                              `client_id` bigint NOT NULL,
                              `agency_id` bigint NOT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `client_id` (`client_id`),
                              UNIQUE KEY `number` (`number`),
                              CONSTRAINT `fk_account_agency` FOREIGN KEY (`agency_id`) REFERENCES `tb_agency` (`id`),
                              CONSTRAINT `fk_account_client` FOREIGN KEY (`client_id`) REFERENCES `tb_client` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;