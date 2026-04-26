CREATE TABLE `tb_loan` (
                           `id` binary(16) NOT NULL,
                           `account_id` bigint NOT NULL,
                           `amount` decimal(19,2) NOT NULL,
                           `interest_rate` decimal(19,2) NOT NULL,
                           `installments` int NOT NULL,
                           `description` varchar(255) DEFAULT NULL,
                           `created_at` datetime DEFAULT NULL,
                           `status` varchar(20) NOT NULL,
                           `paid_installments` int DEFAULT NULL,
                           `total_amount` decimal(19,2) DEFAULT NULL,
                           `installment_amount` decimal(19,2) DEFAULT NULL,
                           `start_date` date DEFAULT NULL,
                           `end_date` date DEFAULT NULL,
                           `manager_id` bigint DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           CONSTRAINT `tb_loan_ibfk_1` FOREIGN KEY (`account_id`) REFERENCES `tb_account` (`id`),
                           CONSTRAINT `tb_loan_ibfk_2` FOREIGN KEY (`manager_id`) REFERENCES `tb_manager` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;