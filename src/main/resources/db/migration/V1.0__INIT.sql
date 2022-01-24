CREATE TABLE `tw_user` (
        `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
        `phone` varchar(13) NOT NULL ,
        `name` varchar(13) NOT NULL ,
        `email` varchar(13) NOT NULL ,
        `status` tinyint(4) unsigned DEFAULT NULL ,
        `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
        `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;