/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;
# 模拟外部银行存款系统的初始化sql脚本
DROP DATABASE IF EXISTS `p2p_bank_depository`;

CREATE DATABASE `p2p_bank_depository` CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';

USE p2p_bank_depository;

# Dump of table balance_details
# ------------------------------------------------------------

DROP TABLE IF EXISTS `balance_details`;

CREATE TABLE `balance_details`
(
    `ID`              BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `USER_NO`         VARCHAR(50)    DEFAULT NULL COMMENT '用户编码,生成唯一,用户在存管系统标识',
    `CHANGE_TYPE`     TINYINT(4)     DEFAULT NULL COMMENT '账户变动类型.1.增加.2.减少.3.冻结.4解冻',
    `AMOUNT`          DECIMAL(10, 2) DEFAULT NULL COMMENT '变动金额',
    `FREEZE_AMOUNT`   DECIMAL(10, 2) DEFAULT NULL COMMENT '冻结金额',
    `BALANCE`         DECIMAL(10, 2) DEFAULT NULL COMMENT '可用余额',
    `APP_CODE`        VARCHAR(50)    DEFAULT NULL COMMENT '应用编码',
    `CREATE_DATE`     DATETIME       DEFAULT NULL COMMENT '账户变动时间',
    `REQUEST_CONTENT` TEXT CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '引起余额变动的冗余业务信息',
    PRIMARY KEY (`ID`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 COMMENT ='用户余额明细表';



# Dump of table bank_card
# ------------------------------------------------------------

DROP TABLE IF EXISTS `bank_card`;

CREATE TABLE `bank_card`
(
    `ID`          BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `USER_ID`     BIGINT(20)  DEFAULT NULL COMMENT '用户标识',
    `BANK_CODE`   VARCHAR(50) NOT NULL COMMENT '银行编码',
    `BANK_NAME`   VARCHAR(50) DEFAULT NULL COMMENT '银行名称',
    `CARD_NUMBER` VARCHAR(50) DEFAULT NULL COMMENT '银行卡号',
    `PASSWORD`    VARCHAR(50) DEFAULT NULL COMMENT '银行卡密码',
    PRIMARY KEY (`ID`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 COMMENT ='银行用户银行卡信息';



# Dump of table bank_card_details
# ------------------------------------------------------------

DROP TABLE IF EXISTS `bank_card_details`;

CREATE TABLE `bank_card_details`
(
    `ID`           BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `BANK_CARD_ID` BIGINT(20) NOT NULL COMMENT '银行卡ID',
    `CHANGE_TYPE`  TINYINT(4)     DEFAULT NULL COMMENT '账户变动类型',
    `MONEY`        DECIMAL(10, 2) DEFAULT NULL COMMENT '变动金额',
    `BALANCE`      DECIMAL(10, 2) DEFAULT NULL COMMENT '当前余额',
    `CREATE_DATE`  DATETIME       DEFAULT NULL COMMENT '账户变动时间',
    PRIMARY KEY (`ID`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 COMMENT ='银行卡明细';



# Dump of table bank_user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `bank_user`;

CREATE TABLE `bank_user`
(
    `ID`        BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `FULLNAME`  VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `ID_NUMBER` VARCHAR(50) DEFAULT NULL COMMENT '身份证号',
    `MOBILE`    VARCHAR(50) DEFAULT NULL COMMENT '银行预留手机号',
    `USER_TYPE` TINYINT(4)  DEFAULT NULL COMMENT '用户类型,个人1or企业0，预留',
    PRIMARY KEY (`ID`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 COMMENT ='银行用户信息表';



# Dump of table claim
# ------------------------------------------------------------

DROP TABLE IF EXISTS `claim`;

CREATE TABLE `claim`
(
    `ID`                    BIGINT(20) NOT NULL COMMENT '主键',
    `PROJECT_ID`            BIGINT(20)  DEFAULT NULL COMMENT '标的标识',
    `PROJECT_NO`            VARCHAR(50) DEFAULT NULL COMMENT '标的编码',
    `CONSUMER_ID`           BIGINT(20) NOT NULL COMMENT '发标人用户标识(冗余)',
    `SOURCE_TENDER_ID`      BIGINT(20) NOT NULL COMMENT '投标信息标识(转让来源)',
    `ROOT_PROJECT_ID`       BIGINT(20)  DEFAULT NULL COMMENT '原始标的标识(冗余)',
    `ROOT_PROJECT_NO`       VARCHAR(50) DEFAULT NULL COMMENT '原始标的编码(冗余)',
    `ASSIGNMENT_REQUEST_NO` VARCHAR(50) DEFAULT NULL COMMENT '债权转让 请求流水号',
    PRIMARY KEY (`ID`),
    KEY `FK_Reference_17` (`PROJECT_ID`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 COMMENT ='债权转让标的附加信息';



# Dump of table depository_bank_card
# ------------------------------------------------------------

DROP TABLE IF EXISTS `depository_bank_card`;

CREATE TABLE `depository_bank_card`
(
    `ID`          BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `USER_ID`     BIGINT(20)  DEFAULT NULL COMMENT '用户标识',
    `BANK_CODE`   VARCHAR(50) NOT NULL COMMENT '银行编码',
    `BANK_NAME`   VARCHAR(50) DEFAULT NULL COMMENT '银行名称',
    `CARD_NUMBER` VARCHAR(50) DEFAULT NULL COMMENT '银行卡号',
    `MOBILE`      VARCHAR(50) DEFAULT NULL COMMENT '银行预留手机号',
    `APP_CODE`    VARCHAR(50) DEFAULT NULL COMMENT '应用编码',
    `REQUEST_NO`  VARCHAR(50) DEFAULT NULL COMMENT '请求流水号',
    PRIMARY KEY (`ID`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 COMMENT ='存管用户绑定银行卡信息';



# Dump of table project
# ------------------------------------------------------------

DROP TABLE IF EXISTS `project`;

CREATE TABLE `project`
(
    `ID`                   BIGINT(20) NOT NULL COMMENT '主键',
    `USER_NO`              VARCHAR(50)                                            DEFAULT NULL COMMENT '发标人用户编码',
    `PROJECT_NO`           VARCHAR(50)                                            DEFAULT NULL COMMENT '标的编码',
    `NAME`                 VARCHAR(50)                                            DEFAULT NULL COMMENT '标的名称',
    `DESCRIPTION`          LONGTEXT COMMENT '标的描述',
    `TYPE`                 VARCHAR(50)                                            DEFAULT NULL COMMENT '标的类型',
    `PERIOD`               INT(11)                                                DEFAULT NULL COMMENT '标的期限(单位:天)',
    `BORROWER_ANNUAL_RATE` DECIMAL(10, 2)                                         DEFAULT NULL COMMENT '年化利率(借款人视图)',
    `REPAYMENT_WAY`        VARCHAR(50)                                            DEFAULT NULL COMMENT '还款方式',
    `AMOUNT`               DECIMAL(10, 2)                                         DEFAULT NULL COMMENT '募集金额',
    `PROJECT_STATUS`       VARCHAR(50)                                            DEFAULT NULL COMMENT '标的状态',
    `CREATE_DATE`          DATETIME                                               DEFAULT NULL COMMENT '创建时间',
    `MODIFY_DATE`          DATETIME                                               DEFAULT NULL COMMENT '修改时间',
    `IS_ASSIGNMENT`        TINYINT(4)                                             DEFAULT NULL COMMENT '是否是债权出让标',
    `REQUEST_NO`           VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '请求流水号',
    PRIMARY KEY (`ID`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 COMMENT ='标的信息表';



# Dump of table recharge_details
# ------------------------------------------------------------

DROP TABLE IF EXISTS `recharge_details`;

CREATE TABLE `recharge_details`
(
    `ID`          BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `REQUEST_NO`  VARCHAR(50)    DEFAULT NULL COMMENT '请求流水号',
    `USER_NO`     VARCHAR(50)    DEFAULT NULL COMMENT '用户编码,生成唯一,用户在存管系统标识',
    `AMOUNT`      DECIMAL(10, 2) DEFAULT NULL COMMENT '金额',
    `CREATE_DATE` DATETIME       DEFAULT NULL COMMENT '触发时间',
    `STATUS`      TINYINT(4)     DEFAULT NULL COMMENT '执行结果',
    `FINISH_DATE` DATETIME       DEFAULT NULL COMMENT '执行返回时间',
    `APP_CODE`    VARCHAR(50)    DEFAULT NULL COMMENT '应用编码',
    PRIMARY KEY (`ID`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 COMMENT ='充值记录表';



# Dump of table request_details
# ------------------------------------------------------------

DROP TABLE IF EXISTS `request_details`;

CREATE TABLE `request_details`
(
    `ID`            BIGINT(20) NOT NULL AUTO_INCREMENT,
    `APP_CODE`      VARCHAR(50) DEFAULT NULL COMMENT '应用编码',
    `REQUEST_NO`    VARCHAR(50) DEFAULT NULL,
    `SERVICE_NAME`  VARCHAR(50) DEFAULT NULL COMMENT '请求类型:1.用户信息、2.绑卡信息',
    `REQUEST_DATA`  TEXT CHARACTER SET utf8 COLLATE utf8_general_ci,
    `RESPONSE_DATA` TEXT,
    `STATUS`        TINYINT(4)  DEFAULT NULL COMMENT '执行结果',
    `CREATE_DATE`   DATETIME    DEFAULT NULL COMMENT '请求时间',
    `FINISH_DATE`   DATETIME    DEFAULT NULL COMMENT '执行返回时间',
    PRIMARY KEY (`ID`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 COMMENT ='存管系统请求信息表';



# Dump of table tender
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tender`;

CREATE TABLE `tender`
(
    `ID`            BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `USER_NO`       VARCHAR(50)                                            DEFAULT NULL COMMENT '投标人用户编码',
    `PROJECT_NO`    VARCHAR(50)                                            DEFAULT NULL COMMENT '标的编码',
    `AMOUNT`        DECIMAL(10, 2)                                         DEFAULT NULL COMMENT '投标冻结金额',
    `TENDER_STATUS` VARCHAR(50)                                            DEFAULT NULL COMMENT '投标状态',
    `CREATE_DATE`   DATETIME                                               DEFAULT NULL COMMENT '创建时间',
    `MODIFY_DATE`   DATETIME                                               DEFAULT NULL COMMENT '修改时间',
    `REQUEST_NO`    VARCHAR(50)                                            DEFAULT NULL COMMENT '投标/债权转让 请求流水号',
    `REMARK`        VARCHAR(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`ID`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 COMMENT ='投标信息表';



# Dump of table user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user`
(
    `ID`           BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `USER_NO`      VARCHAR(50) DEFAULT NULL COMMENT '用户编码,生成唯一,用户在存管系统标识',
    `FULLNAME`     VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `ID_NUMBER`    VARCHAR(50) DEFAULT NULL COMMENT '身份证号',
    `PASSWORD`     VARCHAR(50) DEFAULT NULL COMMENT '存管支付密码',
    `MOBILE`       VARCHAR(50) DEFAULT NULL COMMENT '存管预留手机号',
    `USER_TYPE`    TINYINT(4)  DEFAULT NULL COMMENT '用户类型,个人or企业，预留',
    `ROLE`         VARCHAR(50) DEFAULT NULL COMMENT '用户角色',
    `AUTH_LIST`    VARCHAR(50) DEFAULT NULL COMMENT '授权列表',
    `IS_BIND_CARD` TINYINT(4)  DEFAULT NULL COMMENT '是否已绑定银行卡',
    `APP_CODE`     VARCHAR(50) DEFAULT NULL COMMENT '应用编码',
    `REQUEST_NO`   VARCHAR(50) DEFAULT NULL COMMENT '请求流水号',
    `CREATE_DATE`  DATETIME    DEFAULT NULL COMMENT '产生时间',
    PRIMARY KEY (`ID`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 COMMENT ='存管用户信息表';



# Dump of table withdraw_details
# ------------------------------------------------------------

DROP TABLE IF EXISTS `withdraw_details`;

CREATE TABLE `withdraw_details`
(
    `ID`          BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `REQUEST_NO`  VARCHAR(50)    DEFAULT NULL COMMENT '请求流水号',
    `USER_NO`     VARCHAR(50)    DEFAULT NULL COMMENT '用户编码,生成唯一,用户在存管系统标识',
    `AMOUNT`      DECIMAL(10, 2) DEFAULT NULL COMMENT '金额',
    `COMMISSION`  DECIMAL(10, 2) DEFAULT NULL COMMENT '平台佣金',
    `CREATE_DATE` DATETIME       DEFAULT NULL COMMENT '触发时间',
    `STATUS`      TINYINT(4)     DEFAULT NULL COMMENT '执行结果',
    `FINISH_DATE` DATETIME       DEFAULT NULL COMMENT '执行返回时间',
    `APP_CODE`    VARCHAR(50)    DEFAULT NULL COMMENT '应用编码',
    PRIMARY KEY (`ID`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 COMMENT ='用户余额明细表';


/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;