
CREATE TABLE IF NOT EXISTS `tbl_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(90) NOT NULL,
  `last_name` varchar(90) NOT NULL,
  `email` varchar(130) NOT NULL,
  `password` varchar(130) NOT NULL,
  `temp_token` varchar(255) DEFAULT NULL,
  `status` enum('ACTIVE','INACTIVE','PENDING') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_email_index` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `tbl_provider` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(90) NOT NULL,
  `description` varchar(200) NOT NULL,
  `base_url` varchar(120) NOT NULL,
  `api_key` varchar(255) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `tbl_model` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(120) NOT NULL,
  `provider_id` bigint NOT NULL,
  `type` enum('CHAT','EMBEDDING') NOT NULL,
  `cost_token` decimal(10,6) NOT NULL,
  `cost_token_unit` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `model_provider_index` (`provider_id`),
  CONSTRAINT `model_provider_fk` FOREIGN KEY (`provider_id`) REFERENCES `tbl_provider` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
