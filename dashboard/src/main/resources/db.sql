DROP TABLE IF EXISTS `hk_change_log`;
CREATE TABLE `hk_change_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_key` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '业务key',
  `biz_type` int(11) NOT NULL COMMENT '业务类型：1规则变更；2worker变更',
  `from_str` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '原始值',
  `to_str` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '目标值',
  `app_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '数据所属APP',
  `update_user` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '修改人',
  `create_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `uuid` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '防重ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_uuid`(`uuid`) USING BTREE COMMENT '防重索引'
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;



DROP TABLE IF EXISTS `hk_user`;
CREATE TABLE `hk_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `nick_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '昵称',
  `user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '用户名',
  `pwd` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '密码',
  `phone` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '手机号',
  `role` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '角色：ADMIN-超管，APPADMIN-app管理员，APPUSER-app用户',
  `app_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '所属appName',
  `create_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `state` int(11) NOT NULL DEFAULT 1 COMMENT '状态：1可用；0冻结',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_userName`(`user_name`) USING BTREE COMMENT '账号唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Records of hk_user
-- ----------------------------
INSERT INTO `hk_user` VALUES (1, 'lyfa', 'lyf', '202cb962ac59075b964b07152d234b70', '', 'ADMIN', '', '2020-04-24 02:28:33', 1);



DROP TABLE IF EXISTS `hk_key_rule`;
CREATE TABLE `hk_key_rule`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `key_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'key',
  `prefix` int(11) NOT NULL COMMENT '是否前缀：1是；0否',
  `intervals` int(11) NOT NULL COMMENT '间隔时间（秒）',
  `threshold` int(11) NOT NULL COMMENT '阈值',
  `duration` int(11) NOT NULL DEFAULT 60 COMMENT '缓存时间',
  `app_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '所属appName',
  `state` int(11) NOT NULL COMMENT '状态：1可用；-1删除',
  `update_user` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '修改人',
  `update_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `version` int(11) NOT NULL DEFAULT 0 COMMENT '数据版本',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;


DROP TABLE IF EXISTS `hk_key_record`;
CREATE TABLE `hk_key_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `key_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'key',
  `app_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '所属appName',
  `val` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'value',
  `duration` int(11) NOT NULL DEFAULT 60 COMMENT '缓存时间',
  `source` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '来源',
  `type` int(11) NOT NULL DEFAULT 1 COMMENT '记录类型：1put；2del; -1unkonw',
  `create_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `uuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '防重ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_key`(`uuid`) USING BTREE COMMENT '唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;


DROP TABLE IF EXISTS `hk_change_log`;
CREATE TABLE `hk_change_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_key` varchar(128)  CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT ''  COMMENT '业务ID',
  `biz_type` int(11) NOT NULL COMMENT '业务类型：1规则变更；2worker变更',
  `from_str` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '原始值',
  `to_str` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '目标值',
  `app_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '数据所属APP',
  `update_user` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '修改人',
  `create_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `uuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '防重ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_key`(`uuid`) USING BTREE COMMENT '唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;



DROP TABLE IF EXISTS `hk_key_timely`;
CREATE TABLE `hk_key_timely`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `key_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'key',
  `val` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'value',
  `uuid` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '防重ID',
  `app_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '所属appName',
  `duration` int(11) NOT NULL DEFAULT 0 COMMENT '缓存时间',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_key`(`uuid`) USING BTREE COMMENT '唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Records of hk_key_timely
-- ----------------------------
INSERT INTO `hk_key_timely` VALUES (1, 'testk1', 'testv1', '/jd/hotkeys/test/', 'test', 60000, 1212121);
--INSERT INTO `hk_key_timely` VALUES (2, 'testk2', 'testv2', '/jd/hotkeys/test/', 'test', 10000, 123443345);
