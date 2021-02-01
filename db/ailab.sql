/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.137.159_3306
 Source Server Type    : MySQL
 Source Server Version : 100411
 Source Host           : 192.168.137.159:3306
 Source Schema         : ailab

 Target Server Type    : MySQL
 Target Server Version : 100411
 File Encoding         : 65001

 Date: 31/01/2021 08:33:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for algorithmfilter
-- ----------------------------
DROP TABLE IF EXISTS `algorithmfilter`;
CREATE TABLE `algorithmfilter`  (
  `filterid` int(11) NOT NULL AUTO_INCREMENT,
  `filtername` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '滤波方法：mvav(移动平均)和fodl(一阶滤波)',
  `filteralphe` double NULL DEFAULT NULL COMMENT '一阶滤波参数\r\n',
  `filtertime` int(11) NULL DEFAULT NULL COMMENT '移动平均所用参数\r\n',
  `backtodcstag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '滤波反写的opc位号\r\n',
  `resource` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'opc位号来源',
  `referencepropertyid` int(11) NULL DEFAULT NULL COMMENT '引用的算法属性id',
  PRIMARY KEY (`filterid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for algorithmmodle
-- ----------------------------
DROP TABLE IF EXISTS `algorithmmodle`;
CREATE TABLE `algorithmmodle`  (
  `modleid` int(11) NOT NULL AUTO_INCREMENT,
  `algorithmName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '算法名称',
  `updatetime` timestamp(0) NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`modleid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '算法模型(视频和大数据模型)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for algorithmproperties
-- ----------------------------
DROP TABLE IF EXISTS `algorithmproperties`;
CREATE TABLE `algorithmproperties`  (
  `propertyid` int(11) NOT NULL AUTO_INCREMENT,
  `propertyName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性名称(ch-zh)',
  `property` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性',
  `refrencealgorithmid` int(11) NULL DEFAULT NULL COMMENT '引用的算法id',
  `resource` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'opc数据源',
  `opctag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '反写的opc位号',
  `datatype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性类型，value/image',
  PRIMARY KEY (`propertyid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '算法结果属性表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for company
-- ----------------------------
DROP TABLE IF EXISTS `company`;
CREATE TABLE `company`  (
  `companyId` int(11) NOT NULL AUTO_INCREMENT,
  `commenName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `companyOrder` int(11) NULL DEFAULT NULL,
  `ff` int(11) NULL DEFAULT 8,
  `mv` int(11) NULL DEFAULT 8,
  `pv` int(11) NULL DEFAULT 8,
  PRIMARY KEY (`companyId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of company
-- ----------------------------
INSERT INTO `company` VALUES (1, '桐庐红狮', 1, 8, 8, 8);

-- ----------------------------
-- Table structure for filter
-- ----------------------------
DROP TABLE IF EXISTS `filter`;
CREATE TABLE `filter`  (
  `pk_filterid` int(11) NOT NULL AUTO_INCREMENT COMMENT '滤波器主键',
  `filtername` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '滤波方法：mvav(移动平均)和fodl(一阶滤波),nofilt无滤波',
  `filter_alphe` double NULL DEFAULT NULL COMMENT '一阶滤波系数',
  `filter_time` int(11) NULL DEFAULT NULL COMMENT '移动平均滤波时间',
  `pk_pinid` int(11) NULL DEFAULT NULL COMMENT '滤波器的引脚id主键',
  `backToDCSTag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '反写进dcs的位号',
  `opcresource` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`pk_filterid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of filter
-- ----------------------------
INSERT INTO `filter` VALUES (1, 'fodl', 0.1, 60, 11, '', 'opc192.168.137.159');
INSERT INTO `filter` VALUES (2, 'fodl', 0.1, 60, 29, '', '');
INSERT INTO `filter` VALUES (3, 'fodl', 0.3, NULL, 38, '', '');
INSERT INTO `filter` VALUES (4, 'fodl', 0.1, NULL, 42, '', '');
INSERT INTO `filter` VALUES (5, 'fodl', 0.05, NULL, 46, 'DCS.APC.SLM1.SLM_PV4B', 'opc192.168.137.159');
INSERT INTO `filter` VALUES (6, 'fodl', 0.8, NULL, 64, '', '');
INSERT INTO `filter` VALUES (7, 'fodl', 0.3, NULL, 68, '', '');
INSERT INTO `filter` VALUES (9, 'fodl', 0.3, NULL, 87, '', '');
INSERT INTO `filter` VALUES (10, 'fodl', 0.1, NULL, 91, '', '');
INSERT INTO `filter` VALUES (11, 'fodl', 0.7, NULL, 95, '', '');
INSERT INTO `filter` VALUES (12, 'fodl', 0.7, NULL, 101, '', '');
INSERT INTO `filter` VALUES (13, 'fodl', 0.02, NULL, 108, '', '');
INSERT INTO `filter` VALUES (14, 'fodl', 0.1, NULL, 21, '', '');
INSERT INTO `filter` VALUES (15, 'fodl', 0.1, NULL, 111, '', '');
INSERT INTO `filter` VALUES (16, 'fodl', 0.6, NULL, 118, '', '');
INSERT INTO `filter` VALUES (17, 'fodl', 0.2, NULL, 127, '', '');
INSERT INTO `filter` VALUES (18, 'fodl', 0.05, NULL, 139, '', '');
INSERT INTO `filter` VALUES (19, 'fodl', 0.1, NULL, 143, '', '');
INSERT INTO `filter` VALUES (20, 'fodl', 0.1, NULL, 146, '', '');
INSERT INTO `filter` VALUES (21, 'fodl', 0.02, NULL, 149, '', '');
INSERT INTO `filter` VALUES (22, 'fodl', 0.7, NULL, 152, '', '');

-- ----------------------------
-- Table structure for modle
-- ----------------------------
DROP TABLE IF EXISTS `modle`;
CREATE TABLE `modle`  (
  `modleId` int(11) NOT NULL AUTO_INCREMENT,
  `modleName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `controlAPCOutCycle` int(11) NULL DEFAULT NULL,
  `predicttime_P` int(11) NULL DEFAULT NULL,
  `timeserise_N` int(11) NULL DEFAULT NULL,
  `controltime_M` int(11) NULL DEFAULT NULL,
  `modleEnable` int(11) NULL DEFAULT NULL,
  `runstyle` int(11) NULL DEFAULT 0,
  PRIMARY KEY (`modleId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '控制模型' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modle
-- ----------------------------
INSERT INTO `modle` VALUES (1, '分解炉喂煤控制', 10, 100, 120, 2, 0, 0);
INSERT INTO `modle` VALUES (2, '窑头排转速控制', 10, 65, 80, 2, 0, 0);
INSERT INTO `modle` VALUES (3, '煤磨温度冷热风阀', 12, 70, 90, 2, 0, 1);
INSERT INTO `modle` VALUES (4, '篦冷机篦速控制', 20, 100, 120, 2, 0, 0);
INSERT INTO `modle` VALUES (5, '生料磨产量模型1', 13, 80, 120, 2, 0, 1);
INSERT INTO `modle` VALUES (6, '生料磨温度控制', 15, 70, 90, 2, 1, 0);
INSERT INTO `modle` VALUES (7, '余热锅炉水位控制', 12, 70, 80, 2, 1, 0);
INSERT INTO `modle` VALUES (8, '煤磨产量控制', 15, 60, 80, 2, 1, 1);
INSERT INTO `modle` VALUES (9, '生料磨产量模型2', 10, 10, 100, 2, 0, 0);
INSERT INTO `modle` VALUES (10, '尾排风机转速控制', 10, 80, 100, 2, 0, 0);

-- ----------------------------
-- Table structure for modlepins
-- ----------------------------
DROP TABLE IF EXISTS `modlepins`;
CREATE TABLE `modlepins`  (
  `modlepinsId` int(11) NOT NULL AUTO_INCREMENT,
  `reference_modleId` int(11) NULL DEFAULT NULL,
  `modleOpcTag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '\'NULL\'',
  `modlePinName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '\'NULL\'',
  `opcTagName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'NULL',
  `resource` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `Q` double NULL DEFAULT NULL,
  `dmvHigh` double NULL DEFAULT NULL,
  `deadZone` double NULL DEFAULT NULL,
  `funelinitValue` double NULL DEFAULT NULL,
  `R` double NULL DEFAULT NULL,
  `dmvLow` double NULL DEFAULT NULL,
  `referTrajectoryCoef` double NULL DEFAULT NULL COMMENT '参考轨迹系数（柔化系数）',
  `funneltype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `pinEnable` int(11) NULL DEFAULT 1 COMMENT '引脚使能位，一般用于pv是否启用',
  `updateTime` timestamp(0) NULL DEFAULT current_timestamp(),
  `pintype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'pv sp ff mv pvenable....',
  `tracoefmethod` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '柔化系数方法：\r\nbefore\r\nafter',
  PRIMARY KEY (`modlepinsId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 163 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modlepins
-- ----------------------------
INSERT INTO `modlepins` VALUES (1, 1, 'DCS.APC.FJL.FJL_AM', 'auto', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2021-01-13 12:29:41', 'auto', 'before');
INSERT INTO `modlepins` VALUES (2, 1, 'DCS.APC.FJL.FJL_PV', 'pv1', '分解炉温度', 'opc192.168.137.159', 0.0004, NULL, 0.4, 0.5, NULL, NULL, 0.66, 'fullfunnel', 1, '2020-11-10 15:44:22', 'pv', 'before');
INSERT INTO `modlepins` VALUES (3, 1, '1000', 'pvup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-10 15:44:22', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (4, 1, '600', 'pvdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-10 15:44:22', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (5, 1, 'DCS.APC.FJL.FJL_SV', 'sp1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-10 15:44:22', 'sp', 'before');
INSERT INTO `modlepins` VALUES (6, 1, 'DCS.APC.FJL.FJL_MV', 'mv1', '', 'opc192.168.137.159', NULL, 0.09, NULL, NULL, 20, 0.01, NULL, NULL, 1, '2020-10-30 09:58:19', 'mv', 'before');
INSERT INTO `modlepins` VALUES (7, 1, 'DCS.APC.FJL.FJL_LO', 'mvdown1', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-30 09:58:19', 'mvdown', 'before');
INSERT INTO `modlepins` VALUES (8, 1, 'DCS.APC.FJL.FJL_HI', 'mvup1', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-30 09:58:19', 'mvup', 'before');
INSERT INTO `modlepins` VALUES (9, 1, 'DCS.APC.FJL.FJL_MV', 'mvfb1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-30 09:58:19', 'mvfb', 'before');
INSERT INTO `modlepins` VALUES (10, 2, 'DCS.APC.YTP.YTP_AM', 'auto', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2021-01-13 12:29:56', 'auto', 'before');
INSERT INTO `modlepins` VALUES (11, 2, 'DCS.APC.YTP.YTP_PV', 'pv1', '窑头罩负压', 'opc192.168.137.159', 0.002, NULL, 5, 5, NULL, NULL, 0.7, 'fullfunnel', 1, '2020-10-29 16:54:59', 'pv', 'after');
INSERT INTO `modlepins` VALUES (12, 2, '200', 'pvup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-29 16:54:59', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (13, 2, '-300', 'pvdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-29 16:54:59', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (14, 2, 'DCS.APC.YTP.YTP_SV', 'sp1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-29 16:54:59', 'sp', 'before');
INSERT INTO `modlepins` VALUES (15, 2, 'DCS.APC.YTP.YTP_MV', 'mv1', '', 'opc192.168.137.159', NULL, 2, NULL, NULL, 10, 0.2, NULL, NULL, 1, '2020-10-29 13:06:14', 'mv', 'before');
INSERT INTO `modlepins` VALUES (16, 2, 'DCS.APC.YTP.YTP_LO', 'mvdown1', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-29 13:06:14', 'mvdown', 'before');
INSERT INTO `modlepins` VALUES (17, 2, 'DCS.APC.YTP.YTP_HI', 'mvup1', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-29 13:06:14', 'mvup', 'before');
INSERT INTO `modlepins` VALUES (18, 2, 'DCS.APC.YTP.YTP_MV', 'mvfb1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-29 13:06:14', 'mvfb', 'before');
INSERT INTO `modlepins` VALUES (19, 3, 'DCS.APC.MMT.MMT_AM', 'auto', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2021-01-13 12:30:23', 'auto', 'before');
INSERT INTO `modlepins` VALUES (20, 4, '0', 'auto', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2021-01-27 21:44:19', 'auto', 'before');
INSERT INTO `modlepins` VALUES (21, 3, 'DCS.APC.MMT.MMT_PV', 'pv1', '煤磨入口温度', 'opc192.168.137.159', 0.25, NULL, 4, 2, NULL, NULL, 0.6, 'fullfunnel', 1, '2020-12-02 07:46:50', 'pv', 'after');
INSERT INTO `modlepins` VALUES (22, 3, '300', 'pvup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 07:46:50', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (23, 3, '100', 'pvdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 07:46:50', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (24, 3, 'DCS.APC.MMT.MMT_SV', 'sp1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 07:46:50', 'sp', 'before');
INSERT INTO `modlepins` VALUES (25, 3, 'DCS.APC.MMT.MMT_MV', 'mv1', '冷风阀', 'opc192.168.137.159', NULL, 3, NULL, NULL, 2, 0.5, NULL, NULL, 1, '2020-12-01 13:49:20', 'mv', 'before');
INSERT INTO `modlepins` VALUES (26, 3, 'DCS.APC.MMT.MMT_LO', 'mvdown1', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-01 13:49:20', 'mvdown', 'before');
INSERT INTO `modlepins` VALUES (27, 3, 'DCS.APC.MMT.MMT_HI', 'mvup1', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-01 13:49:20', 'mvup', 'before');
INSERT INTO `modlepins` VALUES (28, 3, 'DCS.APC.MMT.MMT_MV', 'mvfb1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-01 13:49:20', 'mvfb', 'before');
INSERT INTO `modlepins` VALUES (29, 4, 'DCS.APC.BLJ.BLJ_PV', 'pv1', '2室压力', 'opc192.168.137.159', 0.00001, NULL, 50, 70, NULL, NULL, 0.4, 'fullfunnel', 1, '2020-12-19 13:54:14', 'pv', 'before');
INSERT INTO `modlepins` VALUES (30, 4, '10000', 'pvup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-19 13:54:14', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (31, 4, '4000', 'pvdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-19 13:54:14', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (32, 4, 'DCS.APC.BLJ.BLJ_SV', 'sp1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-19 13:54:14', 'sp', 'before');
INSERT INTO `modlepins` VALUES (33, 4, 'DCS.APC.BLJ.BLJ_MV', 'mv1', '', 'opc192.168.137.159', NULL, 0.08, NULL, NULL, 60, 0.01, NULL, NULL, 1, '2020-10-29 16:31:37', 'mv', 'before');
INSERT INTO `modlepins` VALUES (34, 4, 'DCS.APC.BLJ.BLJ_LO', 'mvdown1', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-29 16:31:37', 'mvdown', 'before');
INSERT INTO `modlepins` VALUES (35, 4, 'DCS.APC.BLJ.BLJ_HI', 'mvup1', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-29 16:31:37', 'mvup', 'before');
INSERT INTO `modlepins` VALUES (36, 4, 'DCS.APC.BLJ.BLJ_MV', 'mvfb1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-29 16:31:37', 'mvfb', 'before');
INSERT INTO `modlepins` VALUES (37, 5, 'DCS.APC.SLM1.SLM_AM1', 'auto', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2021-01-13 12:30:45', 'auto', 'before');
INSERT INTO `modlepins` VALUES (38, 5, 'DCS.APC.SLM1.SLM_PV1', 'pv1', '磨内压差', 'opc192.168.137.159', 0.000002, NULL, 25, 25, NULL, NULL, 0.6, 'fullfunnel', 1, '2020-11-19 16:22:07', 'pv', 'before');
INSERT INTO `modlepins` VALUES (39, 5, '-2000', 'pvup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 16:22:07', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (40, 5, '-8000', 'pvdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 16:22:07', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (41, 5, 'DCS.APC.SLM1.SLM_SV1', 'sp1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 16:22:07', 'sp', 'before');
INSERT INTO `modlepins` VALUES (42, 5, 'DCS.APC.SLM1.SLM_PV2', 'pv2', '回料提电流', 'opc192.168.137.159', 1.2, NULL, 2, 1, NULL, NULL, 0.6, 'upfunnel', 1, '2020-12-26 16:03:52', 'pv', 'after');
INSERT INTO `modlepins` VALUES (43, 5, '40', 'pvup2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-26 16:03:52', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (44, 5, '19', 'pvdown2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-26 16:03:52', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (45, 5, 'DCS.APC.SLM1.SLM_SV2', 'sp2', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-26 16:03:52', 'sp', 'before');
INSERT INTO `modlepins` VALUES (46, 5, 'DCS.APC.SLM1.SLM_PV4', 'pv4', '料层厚度', 'opc192.168.137.159', 0.2, NULL, 2, 2, NULL, NULL, 0.6, 'fullfunnel', 1, '2020-12-26 17:59:46', 'pv', 'after');
INSERT INTO `modlepins` VALUES (47, 5, '150', 'pvup4', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-26 17:59:46', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (48, 5, '30', 'pvdown4', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-26 17:59:46', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (49, 5, 'DCS.APC.SLM1.SLM_SV4', 'sp4', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-26 17:59:46', 'sp', 'before');
INSERT INTO `modlepins` VALUES (50, 5, 'DCS.APC.SLM1.SLM_MV1', 'mv1', '产量给定', 'opc192.168.137.159', NULL, 3, NULL, NULL, 40, 0.05, NULL, NULL, 1, '2020-12-26 17:19:16', 'mv', 'before');
INSERT INTO `modlepins` VALUES (51, 5, 'DCS.APC.SLM1.SLM_LO1', 'mvdown1', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-26 17:19:16', 'mvdown', 'before');
INSERT INTO `modlepins` VALUES (52, 5, 'DCS.APC.SLM1.SLM_HI1', 'mvup1', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-26 17:19:16', 'mvup', 'before');
INSERT INTO `modlepins` VALUES (53, 5, 'DCS.APC.SLM1.SLM_MV1', 'mvfb1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-26 17:19:16', 'mvfb', 'before');
INSERT INTO `modlepins` VALUES (54, 6, 'DCS.APC.SLM1.SLM_AM2', 'auto', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2021-01-13 12:31:20', 'auto', 'before');
INSERT INTO `modlepins` VALUES (55, 6, 'DCS.APC.SLM1.SLM_PV3', 'pv1', '出磨温度', 'opc192.168.137.159', 0.009, NULL, 2, 2, NULL, NULL, 0.6, 'fullfunnel', 1, '2020-12-02 08:53:32', 'pv', 'before');
INSERT INTO `modlepins` VALUES (56, 6, '100', 'pvup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 08:53:32', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (57, 6, '60', 'pvdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 08:53:32', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (58, 6, 'DCS.APC.SLM1.SLM_SV3', 'sp1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 08:53:32', 'sp', 'before');
INSERT INTO `modlepins` VALUES (59, 6, 'DCS.APC.SLM1.SLM_MV2', 'mv1', '', 'opc192.168.137.159', NULL, 1, NULL, NULL, 20, 0.1, NULL, NULL, 1, '2020-12-02 11:03:27', 'mv', 'before');
INSERT INTO `modlepins` VALUES (60, 6, 'DCS.APC.SLM1.SLM_LO2', 'mvdown1', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 11:03:27', 'mvdown', 'before');
INSERT INTO `modlepins` VALUES (61, 6, 'DCS.APC.SLM1.SLM_HI2', 'mvup1', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 11:03:27', 'mvup', 'before');
INSERT INTO `modlepins` VALUES (62, 6, 'DCS.APC.SLM1.SLM_MV2FB', 'mvfb1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 11:03:27', 'mvfb', 'before');
INSERT INTO `modlepins` VALUES (63, 7, 'YRAPC.GL.A_GSB_AM', 'auto', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2021-01-13 12:30:59', 'auto', 'before');
INSERT INTO `modlepins` VALUES (64, 7, 'YRAPC.GL.A_AQC_PV', 'pv1', 'AQC液位', 'opc192.168.137.159', 0.00008, NULL, 20, 10, NULL, NULL, 0.7, 'fullfunnel', 1, '2020-10-31 11:08:54', 'pv', 'before');
INSERT INTO `modlepins` VALUES (65, 7, '500', 'pvup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 11:08:54', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (66, 7, '-500', 'pvdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 11:08:54', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (67, 7, 'YRAPC.GL.A_AQC_SP', 'sp1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 11:08:54', 'sp', 'before');
INSERT INTO `modlepins` VALUES (68, 7, 'YRAPC.GL.A_SP_PV', 'pv2', 'SP液位', 'opc192.168.137.159', 0.0009, NULL, 5, 5, NULL, NULL, 0.7, 'fullfunnel', 1, '2020-10-31 14:36:57', 'pv', 'before');
INSERT INTO `modlepins` VALUES (69, 7, '500', 'pvup2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 14:36:57', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (70, 7, '-500', 'pvdown2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 14:36:57', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (71, 7, 'YRAPC.GL.A_SP_SP', 'sp2', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 14:36:57', 'sp', 'before');
INSERT INTO `modlepins` VALUES (72, 7, 'YRAPC.GL.A_AQC_MV', 'pv3', 'AQC阀门开度', 'opc192.168.137.159', 0.0003, NULL, 15, 15, NULL, NULL, 0.6, 'fullfunnel', 0, '2020-10-31 14:56:43', 'pv', 'after');
INSERT INTO `modlepins` VALUES (73, 7, '100', 'pvup3', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 14:56:43', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (74, 7, '0', 'pvdown3', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 14:56:43', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (75, 7, '50', 'sp3', '', 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 14:56:43', 'sp', 'before');
INSERT INTO `modlepins` VALUES (76, 7, 'YRAPC.GL.A_GSB_MV', 'mv1', '给水泵频率', 'opc192.168.137.159', NULL, 0.2, NULL, NULL, 50, 0.01, NULL, NULL, 1, '2020-10-31 13:48:06', 'mv', 'before');
INSERT INTO `modlepins` VALUES (77, 7, 'YRAPC.GL.A_GSB_LO', 'mvdown1', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 13:48:06', 'mvdown', 'before');
INSERT INTO `modlepins` VALUES (78, 7, 'YRAPC.GL.A_GSB_HI', 'mvup1', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 13:48:06', 'mvup', 'before');
INSERT INTO `modlepins` VALUES (79, 7, 'YRAPC.GL.A_GSB_MV', 'mvfb1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 13:48:06', 'mvfb', 'before');
INSERT INTO `modlepins` VALUES (83, 3, 'DCS.APC.MMT.MMT_PV2', 'pv2', '出磨温度', 'opc192.168.137.159', 0.8, NULL, 0.3, 0.3, NULL, NULL, 0.7, 'fullfunnel', 1, '2020-11-19 13:51:28', 'pv', 'before');
INSERT INTO `modlepins` VALUES (84, 3, '80', 'pvup2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 13:51:28', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (85, 3, '40', 'pvdown2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 13:51:28', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (86, 3, 'DCS.APC.MMT.MMT_SV2', 'sp2', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-19 13:51:28', 'sp', 'before');
INSERT INTO `modlepins` VALUES (87, 5, 'DCS.APC.SLM1.SLM_PV5', 'pv5', '主机电流', 'opc192.168.137.159', 0.18, NULL, 1, 1, NULL, NULL, 0.7, 'fullfunnel', 1, '2020-12-26 17:59:37', 'pv', 'after');
INSERT INTO `modlepins` VALUES (88, 5, '200', 'pvup5', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-26 17:59:37', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (89, 5, '100', 'pvdown5', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-26 17:59:37', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (90, 5, 'DCS.APC.SLM1.SLM_SV5', 'sp5', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-26 17:59:37', 'sp', 'before');
INSERT INTO `modlepins` VALUES (91, 6, 'DCS.APC.SLM1.SLM_PV5', 'pv2', '磨机电流', 'opc192.168.137.159', 0.02, NULL, 2, 3, NULL, NULL, 0.6, 'upfunnel', 1, '2020-12-02 09:07:48', 'pv', 'after');
INSERT INTO `modlepins` VALUES (92, 6, '200', 'pvup2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 09:07:48', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (93, 6, '130', 'pvdown2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 09:07:48', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (94, 6, 'DCS.APC.SLM1.SLM_SV5', 'sp2', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 09:07:48', 'sp', 'before');
INSERT INTO `modlepins` VALUES (95, 7, 'YRAPC.GL.A_AQC_WD', 'ff1', 'AQC温度', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 12:58:20', 'ff', 'before');
INSERT INTO `modlepins` VALUES (96, 7, '450', 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 12:58:20', 'ffup', 'before');
INSERT INTO `modlepins` VALUES (97, 7, '350', 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 12:58:20', 'ffdown', 'before');
INSERT INTO `modlepins` VALUES (98, 3, 'DCS.APC.MMT.MMT_PV', 'ff1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 13:22:13', 'ff', 'before');
INSERT INTO `modlepins` VALUES (99, 3, '260', 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 13:22:13', 'ffup', 'before');
INSERT INTO `modlepins` VALUES (100, 3, '180', 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 13:22:13', 'ffdown', 'before');
INSERT INTO `modlepins` VALUES (101, 7, 'YRAPC.GL.A_AQC_MV', 'ff2', 'AQC阀门', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 14:58:27', 'ff', 'before');
INSERT INTO `modlepins` VALUES (102, 7, '100', 'ffup2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 14:58:27', 'ffup', 'before');
INSERT INTO `modlepins` VALUES (103, 7, '0', 'ffdown2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-10-31 14:58:27', 'ffdown', 'before');
INSERT INTO `modlepins` VALUES (104, 3, 'DCS.APC.MMT.MMT_PV', 'pv3', '煤磨入口温度', 'opc192.168.137.159', 0.2, NULL, 6, 6, NULL, NULL, 0.6, 'fullfunnel', 1, '2020-12-01 14:30:51', 'pv', 'after');
INSERT INTO `modlepins` VALUES (105, 3, '280', 'pvup3', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-01 14:30:51', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (106, 3, '150', 'pvdown3', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-01 14:30:51', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (107, 3, 'DCS.APC.MMT.MMT_SV', 'sp3', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-01 14:30:51', 'sp', 'before');
INSERT INTO `modlepins` VALUES (108, 1, 'DCS.APC.FJL.FJL_FF', 'ff1', '风压', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-10 16:11:09', 'ff', 'before');
INSERT INTO `modlepins` VALUES (109, 1, '25000', 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-10 16:11:09', 'ffup', 'before');
INSERT INTO `modlepins` VALUES (110, 1, '16000', 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-10 16:11:09', 'ffdown', 'before');
INSERT INTO `modlepins` VALUES (111, 3, 'DCS.APC.MMT.MMT_FD', 'ff2', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-20 08:09:14', 'ff', 'before');
INSERT INTO `modlepins` VALUES (112, 3, '50', 'ffup2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-20 08:09:14', 'ffup', 'before');
INSERT INTO `modlepins` VALUES (113, 3, '35', 'ffdown2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-11-20 08:09:14', 'ffdown', 'before');
INSERT INTO `modlepins` VALUES (114, 3, 'DCS.APC.MMT.MMT_MV2', 'mv2', '热风阀', 'opc192.168.137.159', NULL, 2, NULL, NULL, 5, 0.4, NULL, NULL, 1, '2020-12-02 07:45:16', 'mv', 'before');
INSERT INTO `modlepins` VALUES (115, 3, 'DCS.APC.MMT.MMT_LO2', 'mvdown2', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 07:45:16', 'mvdown', 'before');
INSERT INTO `modlepins` VALUES (116, 3, 'DCS.APC.MMT.MMT_HI2', 'mvup2', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 07:45:16', 'mvup', 'before');
INSERT INTO `modlepins` VALUES (117, 3, 'DCS.APC.MMT.MMT_MV2', 'mvfb2', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 07:45:16', 'mvfb', 'before');
INSERT INTO `modlepins` VALUES (118, 3, 'DCS.APC.MMT.MMT_PV2', 'pv4', '出口温度', 'opc192.168.137.159', 2, NULL, 1, 0.6, NULL, NULL, 0.6, 'downfunnel', 1, '2020-12-01 15:39:00', 'pv', 'before');
INSERT INTO `modlepins` VALUES (119, 3, '80', 'pvup4', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-01 15:39:00', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (120, 3, '30', 'pvdown4', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-01 15:39:00', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (121, 3, 'DCS.APC.MMT.MMT_SV2', 'sp4', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-01 15:39:00', 'sp', 'before');
INSERT INTO `modlepins` VALUES (122, 8, 'DCS.APC.MMT.MMF_AM', 'auto', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2021-01-13 12:31:08', 'auto', 'before');
INSERT INTO `modlepins` VALUES (123, 8, 'DCS.APC.MMT.MMT_PV2', 'pv1', '出磨温度', 'opc192.168.137.159', 0.4, NULL, 1.2, 0.8, NULL, NULL, 0.7, 'fullfunnel', 1, '2020-12-28 17:56:43', 'pv', 'before');
INSERT INTO `modlepins` VALUES (124, 8, '70', 'pvup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 17:56:43', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (125, 8, '50', 'pvdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 17:56:43', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (126, 8, 'DCS.APC.MMT.MMT_SV2', 'sp1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 17:56:43', 'sp', 'before');
INSERT INTO `modlepins` VALUES (127, 8, 'DCS.APC.MMT.MMF_PV', 'pv2', '温度偏差', 'opc192.168.137.159', 0.25, NULL, 12, 5, NULL, NULL, 0.7, 'fullfunnel', 1, '2020-12-28 15:29:29', 'pv', 'before');
INSERT INTO `modlepins` VALUES (128, 8, '50', 'pvup2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 15:29:29', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (129, 8, '-50', 'pvdown2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 15:29:29', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (130, 8, '1', 'sp2', '', 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 15:29:29', 'sp', 'before');
INSERT INTO `modlepins` VALUES (131, 8, 'DCS.APC.MMT.MMF_MV', 'mv1', '', 'opc192.168.137.159', NULL, 0.5, NULL, NULL, 50, 0.1, NULL, NULL, 1, '2020-12-28 17:58:04', 'mv', 'before');
INSERT INTO `modlepins` VALUES (132, 8, 'DCS.APC.MMT.MMF_LO', 'mvdown1', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 17:58:04', 'mvdown', 'before');
INSERT INTO `modlepins` VALUES (133, 8, 'DCS.APC.MMT.MMF_HI', 'mvup1', NULL, 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 17:58:04', 'mvup', 'before');
INSERT INTO `modlepins` VALUES (134, 8, 'DCS.APC.MMT.MMF_MV', 'mvfb1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 17:58:04', 'mvfb', 'before');
INSERT INTO `modlepins` VALUES (135, 8, 'DCS.APC.MMT.MMT_PV2', 'pv3', '出磨温度', 'opc192.168.137.159', 0.8, NULL, 0.5, 0.5, NULL, NULL, 0.6, 'upfunnel', 1, '2020-12-28 15:34:01', 'pv', 'before');
INSERT INTO `modlepins` VALUES (136, 8, '70', 'pvup3', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 15:34:01', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (137, 8, '50', 'pvdown3', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 15:34:01', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (138, 8, 'DCS.APC.MMT.MMT_SV2', 'sp3', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 15:34:01', 'sp', 'before');
INSERT INTO `modlepins` VALUES (139, 6, 'DCS.APC.SLM1.SLM_PV4', 'pv3', '料层厚度', 'opc192.168.137.159', 0.018, NULL, 3, 4, NULL, NULL, 0.6, 'fullfunnel', 1, '2020-12-02 10:27:29', 'pv', 'after');
INSERT INTO `modlepins` VALUES (140, 6, '180', 'pvup3', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 10:27:29', 'pvup', 'before');
INSERT INTO `modlepins` VALUES (141, 6, '50', 'pvdown3', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 10:27:29', 'pvdown', 'before');
INSERT INTO `modlepins` VALUES (142, 6, 'DCS.APC.SLM1.SLM_SV4', 'sp3', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-02 10:27:29', 'sp', 'before');
INSERT INTO `modlepins` VALUES (143, 5, 'DCS.APC.SLM1.A41E1AH05I', 'ff1', '循环风机电流', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-25 10:45:00', 'ff', 'before');
INSERT INTO `modlepins` VALUES (144, 5, '200', 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-25 10:45:00', 'ffup', 'before');
INSERT INTO `modlepins` VALUES (145, 5, '160', 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-25 10:45:00', 'ffdown', 'before');
INSERT INTO `modlepins` VALUES (146, 5, 'DCS.APC.SLM1.AI4105101S', 'ff2', '选粉机转速', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-25 10:45:44', 'ff', 'before');
INSERT INTO `modlepins` VALUES (147, 5, '1300', 'ffup2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-25 10:45:44', 'ffup', 'before');
INSERT INTO `modlepins` VALUES (148, 5, '1000', 'ffdown2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-25 10:45:44', 'ffdown', 'before');
INSERT INTO `modlepins` VALUES (149, 5, 'DCS.APC.SLM1.SLM_PV4', 'ff3', '料层厚度', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-26 17:41:50', 'ff', 'before');
INSERT INTO `modlepins` VALUES (150, 5, '200', 'ffup3', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-26 17:41:50', 'ffup', 'before');
INSERT INTO `modlepins` VALUES (151, 5, '30', 'ffdown3', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-26 17:41:50', 'ffdown', 'before');
INSERT INTO `modlepins` VALUES (152, 8, 'DCS.APC.MMT.MMT_PV', 'ff1', '', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 15:48:41', 'ff', 'before');
INSERT INTO `modlepins` VALUES (153, 8, '260', 'ffup1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 15:48:41', 'ffup', 'before');
INSERT INTO `modlepins` VALUES (154, 8, '180', 'ffdown1', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 15:48:41', 'ffdown', 'before');
INSERT INTO `modlepins` VALUES (155, 8, 'DCS.APC.MMT.MMT_MV', 'ff2', '冷风阀', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 15:59:37', 'ff', 'before');
INSERT INTO `modlepins` VALUES (156, 8, '100', 'ffup2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 15:59:37', 'ffup', 'before');
INSERT INTO `modlepins` VALUES (157, 8, '0', 'ffdown2', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 15:59:37', 'ffdown', 'before');
INSERT INTO `modlepins` VALUES (158, 8, 'DCS.APC.MMT.MMT_MV2', 'ff3', '热风阀', 'opc192.168.137.159', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 16:00:16', 'ff', 'before');
INSERT INTO `modlepins` VALUES (159, 8, '100', 'ffup3', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 16:00:16', 'ffup', 'before');
INSERT INTO `modlepins` VALUES (160, 8, '0', 'ffdown3', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2020-12-28 16:00:16', 'ffdown', 'before');
INSERT INTO `modlepins` VALUES (161, 9, '0', 'auto', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2021-01-13 12:31:47', 'auto', 'before');
INSERT INTO `modlepins` VALUES (162, 10, '0', 'auto', NULL, 'constant', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, '2021-01-13 12:32:49', 'auto', 'before');

-- ----------------------------
-- Table structure for modlerespon
-- ----------------------------
DROP TABLE IF EXISTS `modlerespon`;
CREATE TABLE `modlerespon`  (
  `modletagId` int(11) NOT NULL AUTO_INCREMENT,
  `refrencemodleId` int(11) NULL DEFAULT NULL COMMENT '模型id',
  `stepRespJson` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `inputPins` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '引脚名称：mvn ffn等n=1,2,3..8',
  `outputPins` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '引脚名称：pvn 等n=1,2,3..8',
  `effectRatio` double NULL DEFAULT 1,
  PRIMARY KEY (`modletagId`) USING BTREE,
  INDEX `refrencemodleId`(`refrencemodleId`) USING BTREE,
  CONSTRAINT `modlerespon_ibfk_1` FOREIGN KEY (`refrencemodleId`) REFERENCES `modle` (`modleId`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modlerespon
-- ----------------------------
INSERT INTO `modlerespon` VALUES (1, 2, '{\"tao\":20.0,\"t\":160.0,\"k\":-7.0}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (2, 1, '{\"tao\":34.0,\"t\":185.0,\"k\":27.0}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (3, 3, '{\"tao\":30.0,\"t\":170.0,\"k\":-2.0}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (4, 4, '{\"tao\":70.0,\"t\":620.0,\"k\":-2600.0}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (5, 5, '{\"tao\":60.0,\"t\":260.0,\"k\":50.0}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (6, 5, '{\"tao\":60.0,\"t\":240.0,\"k\":0.5}', 'mv1', 'pv2', 2);
INSERT INTO `modlerespon` VALUES (7, 5, '{\"tao\":60.0,\"t\":280.0,\"k\":5.0}', 'mv1', 'pv4', 1);
INSERT INTO `modlerespon` VALUES (8, 6, '{\"tao\":30.0,\"t\":255.0,\"k\":-2.0}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (9, 7, '{\"tao\":70.0,\"t\":360.0,\"k\":36.0}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (10, 7, '{\"tao\":145.0,\"t\":600.0,\"k\":60.0}', 'mv1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (11, 7, '{\"tao\":200.0,\"t\":550.0,\"k\":-3.0}', 'mv1', 'pv3', 1);
INSERT INTO `modlerespon` VALUES (13, 3, '{\"tao\":37.0,\"t\":230.0,\"k\":-0.32}', 'mv1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (14, 5, '{\"tao\":60.0,\"t\":260.0,\"k\":7.0}', 'mv1', 'pv5', 2);
INSERT INTO `modlerespon` VALUES (15, 6, '{\"tao\":20.0,\"t\":180.0,\"k\":10.0}', 'mv1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (16, 7, '{\"tao\":45.0,\"t\":370.0,\"k\":-6.0}', 'ff1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (17, 3, '{\"tao\":30.0,\"t\":240.0,\"k\":0.19}', 'ff1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (18, 7, '{\"tao\":80.0,\"t\":500.0,\"k\":-5.0}', 'ff2', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (19, 3, '{\"tao\":30.0,\"t\":170.0,\"k\":4.5}', 'mv2', 'pv3', 1);
INSERT INTO `modlerespon` VALUES (20, 1, '{\"tao\":35.0,\"t\":150.0,\"k\":0.06}', 'ff1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (21, 3, '{\"tao\":80.0,\"t\":190.0,\"k\":-1.2}', 'ff2', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (22, 3, '{\"tao\":35.0,\"t\":220.0,\"k\":0.32}', 'mv2', 'pv4', 1);
INSERT INTO `modlerespon` VALUES (23, 8, '{\"tao\":60.0,\"t\":380.0,\"k\":-3.8}', 'mv1', 'pv1', 2);
INSERT INTO `modlerespon` VALUES (24, 8, '{\"tao\":60.0,\"t\":320.0,\"k\":-4.5}', 'mv1', 'pv2', 2);
INSERT INTO `modlerespon` VALUES (25, 8, '{\"tao\":50.0,\"t\":300.0,\"k\":-3.5}', 'mv1', 'pv3', 1);
INSERT INTO `modlerespon` VALUES (26, 6, '{\"tao\":20.0,\"t\":180.0,\"k\":20.0}', 'mv1', 'pv3', 1);
INSERT INTO `modlerespon` VALUES (27, 5, '{\"tao\":30.0,\"t\":180.0,\"k\":-5.0}', 'ff1', 'pv4', 1);
INSERT INTO `modlerespon` VALUES (28, 5, '{\"tao\":30.0,\"t\":240.0,\"k\":3.0}', 'ff2', 'pv4', 1);
INSERT INTO `modlerespon` VALUES (29, 5, '{\"tao\":30.0,\"t\":190.0,\"k\":-2.0}', 'ff1', 'pv5', 1);
INSERT INTO `modlerespon` VALUES (30, 5, '{\"tao\":30.0,\"t\":240.0,\"k\":2.0}', 'ff2', 'pv5', 1);
INSERT INTO `modlerespon` VALUES (31, 5, '{\"tao\":25.0,\"t\":100.0,\"k\":-0.2}', 'ff3', 'pv5', 1);
INSERT INTO `modlerespon` VALUES (32, 8, '{\"tao\":30.0,\"t\":240.0,\"k\":0.3}', 'ff1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (33, 8, '{\"tao\":35.0,\"t\":240.0,\"k\":-0.37}', 'ff2', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (34, 8, '{\"tao\":30.0,\"t\":270.0,\"k\":0.37}', 'ff3', 'pv1', 1);

-- ----------------------------
-- Table structure for opcserveinfo
-- ----------------------------
DROP TABLE IF EXISTS `opcserveinfo`;
CREATE TABLE `opcserveinfo`  (
  `opcserveid` int(11) NOT NULL AUTO_INCREMENT,
  `opcuser` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `opcpassword` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `opcip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `opcclsid` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`opcserveid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'opcserver的信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of opcserveinfo
-- ----------------------------
INSERT INTO `opcserveinfo` VALUES (1, 'administrator', 'supcondcs', '192.168.137.159', '6E6170F0-FF2D-11D2-8087-00105AA8F840');

-- ----------------------------
-- Table structure for opcverification
-- ----------------------------
DROP TABLE IF EXISTS `opcverification`;
CREATE TABLE `opcverification`  (
  `tagid` int(11) NOT NULL AUTO_INCREMENT,
  `tagName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `tag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'opc验证位号',
  `opcserveid` int(11) NULL DEFAULT NULL COMMENT 'opc serve的id',
  PRIMARY KEY (`tagid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'opc验证位号表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of opcverification
-- ----------------------------
INSERT INTO `opcverification` VALUES (1, '通讯校验', 'DCS.APC.A_TXJY', 1);
INSERT INTO `opcverification` VALUES (2, '', 'YRAPC.GL.A_TXJY', 1);

-- ----------------------------
-- Table structure for shockdetect
-- ----------------------------
DROP TABLE IF EXISTS `shockdetect`;
CREATE TABLE `shockdetect`  (
  `pk_shockdetectid` int(11) NOT NULL AUTO_INCREMENT,
  `pk_pinid` int(11) NULL DEFAULT NULL COMMENT '引用的过滤器主键id',
  `backToDCSTag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '有效幅值计算结果反写位号',
  `opcresource` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '有效幅值opc反写位号源',
  `dampcoeff` double NULL DEFAULT NULL COMMENT '动态阻尼系数\r\n',
  `windowstime` int(11) NULL DEFAULT NULL COMMENT '窗口时间',
  `filtercoeff` double NULL DEFAULT NULL COMMENT '一阶滤波系数',
  `enable` int(11) NULL DEFAULT NULL COMMENT '是否启用',
  `filterbacktodcstag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '滤波后数据反写位号',
  `filteropcresource` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '滤波数据反写位号opc源',
  PRIMARY KEY (`pk_shockdetectid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '震荡检测' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shockdetect
-- ----------------------------
INSERT INTO `shockdetect` VALUES (1, 29, '', '', 0.1, 150, 0.3, 1, '', '');
INSERT INTO `shockdetect` VALUES (2, 38, '', '', NULL, 150, 0.1, 1, '', '');

SET FOREIGN_KEY_CHECKS = 1;
