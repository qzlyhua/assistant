CREATE TABLE `AS_API_CSR`
(
    `id`                 int(11) NOT NULL AUTO_INCREMENT,
    `path`               varchar(100)  DEFAULT NULL COMMENT '接口路径',
    `name`               varchar(100)  DEFAULT NULL COMMENT '接口名称',
    `description`        varchar(1000) DEFAULT NULL COMMENT '功能描述',
    `remarks`            varchar(1000) DEFAULT NULL COMMENT '补充说明',
    `req_params_example` text COMMENT '入参举例',
    `res_params_example` text COMMENT '出参举例',
    `version`            varchar(255)  DEFAULT NULL COMMENT '版本',
    `business_area`      varchar(255)  DEFAULT NULL COMMENT '业务领域',
    `business_sub_area`  varchar(255)  DEFAULT NULL COMMENT '二级业务领域',
    `update_time`        datetime      DEFAULT NULL COMMENT '修改时间',
    `create_time`        datetime      DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='接口传输规范定义';

CREATE TABLE `AS_API_CSR_PARAM`
(
    `id`             int(11)      NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `csr_id`         int(11)      NOT NULL COMMENT '传输规范ID',
    `parameter_type` varchar(10)  NOT NULL COMMENT '参数类型（入参、出参）',
    `key`            varchar(100) NOT NULL COMMENT '属性名',
    `type`           varchar(100)  DEFAULT NULL COMMENT '类型',
    `describe`       varchar(1000) DEFAULT NULL COMMENT '描述',
    `required`       varchar(50)   DEFAULT NULL COMMENT '必填说明',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `AS_API_CSR_DIC`
(
    `id`   int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type` varchar(255) NOT NULL COMMENT '字典类别',
    `code` varchar(255) NOT NULL COMMENT '代码',
    `name` varchar(255) NOT NULL COMMENT '含义',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4;