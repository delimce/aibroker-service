CREATE TABLE IF NOT EXISTS `tbl_user_request` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `model_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `prompt` text NOT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_request_model_index` (`model_id`),
  KEY `user_request_user_index` (`user_id`),
  CONSTRAINT `user_request_model_fk` FOREIGN KEY (`model_id`) REFERENCES `tbl_model` (`id`),
  CONSTRAINT `user_request_user_fk` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `tbl_request_metric` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `request_id` bigint NOT NULL,
  `completion_tokens` int DEFAULT NULL,
  `prompt_tokens` int DEFAULT NULL,
  `total_tokens` int DEFAULT NULL,
  `prompt_cache_hit_tokens` int DEFAULT NULL,
  `prompt_cache_miss_tokens` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `request_metric_index` (`request_id`),
  CONSTRAINT `request_metric_fk` FOREIGN KEY (`request_id`) REFERENCES `tbl_user_request` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;