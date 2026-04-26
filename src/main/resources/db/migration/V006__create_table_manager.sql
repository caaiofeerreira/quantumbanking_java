CREATE TABLE `tb_manager` (
                              `id` bigint NOT NULL,
                              `agency_id` bigint NOT NULL,
                              PRIMARY KEY (`id`),
                              CONSTRAINT `fk_manager_agency` FOREIGN KEY (`agency_id`) REFERENCES `tb_agency` (`id`),
                              CONSTRAINT `fk_manager_user` FOREIGN KEY (`id`) REFERENCES `tb_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;