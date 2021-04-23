CREATE TABLE `co_company`
(
    `id`                   varchar(40)  NOT NULL COMMENT 'ID',
    `name`                 varchar(255) NOT NULL COMMENT '公司名称',
    `manager_id`           varchar(255) NOT NULL COMMENT '企业登录账号ID',
    `version`              varchar(255)          DEFAULT NULL COMMENT '当前版本',
    `renewal_date`         datetime              DEFAULT NULL COMMENT '续期时间',
    `expiration_date`      datetime              DEFAULT NULL COMMENT '到期时间',
    `company_area`         varchar(255)          DEFAULT NULL COMMENT '公司地区',
    `company_address`      text COMMENT '公司地址',
    `business_license_id`  varchar(255)          DEFAULT NULL COMMENT '营业执照-图片ID',
    `legal_representative` varchar(255)          DEFAULT NULL COMMENT '法人代表',
    `company_phone`        varchar(255)          DEFAULT NULL COMMENT '公司电话',
    `mailbox`              varchar(255)          DEFAULT NULL COMMENT '邮箱',
    `company_size`         varchar(255)          DEFAULT NULL COMMENT '公司规模',
    `industry`             varchar(255)          DEFAULT NULL COMMENT '所属行业',
    `remarks`              text COMMENT '备注',
    `audit_state`          varchar(255)          DEFAULT NULL COMMENT '审核状态',
    `state`                tinyint(2)   NOT NULL DEFAULT '1' COMMENT '状态',
    `balance`              double       NOT NULL COMMENT '当前余额',
    `create_time`          datetime     NOT NULL COMMENT '创建时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;