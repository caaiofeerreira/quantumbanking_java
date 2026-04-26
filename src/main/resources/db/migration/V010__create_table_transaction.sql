CREATE TABLE `tb_transaction` (
                                  `id` binary(16) NOT NULL,
                                  `account_origin_id` bigint DEFAULT NULL,
                                  `account_destiny_id` bigint DEFAULT NULL,
                                  `destiny_name` varchar(255) DEFAULT NULL,
                                  `destiny_account` varchar(50) DEFAULT NULL,
                                  `destiny_agency` varchar(20) DEFAULT NULL,
                                  `bank_code` varchar(3) DEFAULT NULL,
                                  `destiny_document` varchar(20) DEFAULT NULL,
                                  `amount` decimal(19,2) DEFAULT NULL,
                                  `type` varchar(20) NOT NULL,
                                  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                  `description` varchar(255) DEFAULT NULL,
                                  PRIMARY KEY (`id`),
                                  CONSTRAINT `fk_trans_destiny` FOREIGN KEY (`account_destiny_id`) REFERENCES `tb_account` (`id`),
                                  CONSTRAINT `fk_trans_origin` FOREIGN KEY (`account_origin_id`) REFERENCES `tb_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;