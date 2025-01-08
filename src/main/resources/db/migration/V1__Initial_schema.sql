CREATE TABLE `user` (
                        `id` int NOT NULL AUTO_INCREMENT,
                        `user_id` varchar(255) NOT NULL,
                        `user_name` varchar(100) NOT NULL,
                        `password` varchar(255) DEFAULT NULL,
                        `nick_name` varchar(255) NOT NULL,
                        `email_provider` varchar(255) DEFAULT NULL,
                        `role` varchar(255) NOT NULL,
                        `email` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

