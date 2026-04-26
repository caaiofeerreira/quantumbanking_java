CREATE TABLE `tb_agency` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `agency_name` varchar(100) DEFAULT NULL,
                             `agency_number` varchar(4) DEFAULT NULL,
                             `phone` varchar(20) DEFAULT NULL,
                             `banking_id` bigint NOT NULL,
                             `street` varchar(255) DEFAULT NULL,
                             `number` varchar(10) DEFAULT NULL,
                             `complement` varchar(100) DEFAULT NULL,
                             `neighborhood` varchar(100) DEFAULT NULL,
                             `city` varchar(50) DEFAULT NULL,
                             `state` varchar(2) DEFAULT NULL,
                             `zip_code` varchar(9) DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             CONSTRAINT `fk_agency_bank` FOREIGN KEY (`banking_id`) REFERENCES `tb_bank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;