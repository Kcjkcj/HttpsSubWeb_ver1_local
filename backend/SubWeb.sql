create database subweb;
use subweb;

DROP TABLE IF EXISTS `account`;

CREATE TABLE `account` (
  `account_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `pwd` varchar(200) NOT NULL,
  `email` varchar(100) NOT NULL,
  `create_dt` date NOT NULL,
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `role` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(45) NOT NULL,
  `account_id` int NOT NULL,
  PRIMARY KEY (`role_id`),
  KEY `account_id_idx` (`account_id`),
  CONSTRAINT `account_id` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `friend_list` (
  `friend_list_id` int NOT NULL AUTO_INCREMENT,
  `my_account_id` int NOT NULL,
  `friend_account_id` int NOT NULL,
  `create_dt` date NOT NULL,
  PRIMARY KEY (`friend_list_id`),
  KEY `fk_friend_list_accounts1_idx` (`my_account_id`),
  KEY `fk_friend_list_accounts2_idx` (`friend_account_id`),
  CONSTRAINT `fk_friend_list_accounts1` FOREIGN KEY (`my_account_id`) REFERENCES `account` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_friend_list_accounts2` FOREIGN KEY (`friend_account_id`) REFERENCES `account` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;


CREATE TABLE `subculture` (
  `subculture_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `genre` varchar(45) NOT NULL,
  `image_path` varchar(1000) DEFAULT NULL,
  `create_dt` date NOT NULL,
  PRIMARY KEY (`subculture_id`),
  UNIQUE KEY `title_UNIQUE` (`title`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `post` (
  `post_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(45) NOT NULL,
  `create_dt` date NOT NULL,
  `post_body` mediumtext,
  `subculture_id` int NOT NULL,
  `account_id` int DEFAULT NULL,
  `is_notice` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`post_id`),
  KEY `fk_post_subculture_idx` (`subculture_id`),
  KEY `fk_post_account1_idx` (`account_id`),
  CONSTRAINT `fk_post_account1` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_post_subculture` FOREIGN KEY (`subculture_id`) REFERENCES `subculture` (`subculture_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;


CREATE TABLE `message` (
  `id` int NOT NULL AUTO_INCREMENT,
  `send_id` int DEFAULT NULL,
  `receive_id` int DEFAULT NULL,
  `is_notice` tinyint NOT NULL,
  `is_request` tinyint NOT NULL,
  `message_body` mediumtext NOT NULL,
  `create_dt` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_message_account_idx` (`send_id`),
  KEY `fk_message_account1_idx` (`receive_id`),
  CONSTRAINT `fk_message_account` FOREIGN KEY (`send_id`) REFERENCES `account` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_message_account1` FOREIGN KEY (`receive_id`) REFERENCES `account` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `comment` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `comment_body` text,
  `create_dt` date NOT NULL,
  `account_id` int DEFAULT NULL,
  `post_id` int NOT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `fk_comment_account1_idx` (`account_id`),
  KEY `fk_comment_post1_idx` (`post_id`),
  CONSTRAINT `fk_comment_account1` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_post1` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb3;






