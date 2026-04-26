CREATE TABLE `tb_client` (
                             `id` bigint NOT NULL,
                             `type` varchar(20) DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             CONSTRAINT `fk_client_user` FOREIGN KEY (`id`) REFERENCES `tb_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;