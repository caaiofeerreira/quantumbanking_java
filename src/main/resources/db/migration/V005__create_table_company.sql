CREATE TABLE `tb_company` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `company_name` varchar(255) NOT NULL,
                              `trade_name` varchar(255) NOT NULL,
                              `cnpj` varchar(18) NOT NULL,
                              `state_registration` varchar(20) NOT NULL,
                              `street` varchar(255) DEFAULT NULL,
                              `number` varchar(10) DEFAULT NULL,
                              `complement` varchar(255) DEFAULT NULL,
                              `neighborhood` varchar(255) DEFAULT NULL,
                              `city` varchar(255) DEFAULT NULL,
                              `state` varchar(2) DEFAULT NULL,
                              `zip_code` varchar(9) DEFAULT NULL,
                              `client_id` bigint NOT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `cnpj` (`cnpj`),
                              UNIQUE KEY `client_id` (`client_id`),
                              CONSTRAINT `tb_company_ibfk_1` FOREIGN KEY (`client_id`) REFERENCES `tb_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;