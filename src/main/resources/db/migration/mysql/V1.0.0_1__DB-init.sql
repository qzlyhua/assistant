CREATE TABLE `AS_RE_ORIGIN`
(
    `id`          int(10)     NOT NULL AUTO_INCREMENT,
    `origin_code` varchar(10) NOT NULL,
    `origin_name` varchar(50) NOT NULL,
    `auth_code`   varchar(4)   DEFAULT NULL,
    `address`     varchar(100) DEFAULT NULL,
    `env_type`    varchar(10)  DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 9
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `AS_RE_USER`
(
    `id`       int(10) NOT NULL AUTO_INCREMENT,
    `username` varchar(10)  DEFAULT NULL,
    `password` varchar(255) DEFAULT NULL,
    `type`     int(2)  NOT NULL COMMENT '1：管理员，2：普通用户，9：钉钉用户',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `AS_CFG_DB`
(
    `id`        int(11) NOT NULL AUTO_INCREMENT,
    `env_type`  varchar(10)  DEFAULT NULL,
    `db_type`   varchar(10)  DEFAULT NULL,
    `db_schema` varchar(50)  DEFAULT NULL,
    `url`       varchar(200) DEFAULT NULL,
    `user`      varchar(50)  DEFAULT NULL,
    `pwd`       varchar(50)  DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4;