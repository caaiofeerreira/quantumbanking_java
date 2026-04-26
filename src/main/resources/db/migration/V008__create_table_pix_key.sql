CREATE TABLE `tb_pix_key` (
                              `id` binary(16) NOT NULL,
                              `pix_key` varchar(100) NOT NULL,
                              `type` varchar(20) NOT NULL,
                              `account_id` bigint NOT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `pix_key` (`pix_key`),
                              CONSTRAINT `tb_pix_key_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `tb_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;