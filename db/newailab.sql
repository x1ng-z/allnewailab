/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.137.159_3306
 Source Server Type    : MySQL
 Source Server Version : 100411
 Source Host           : 192.168.137.159:3306
 Source Schema         : newailab

 Target Server Type    : MySQL
 Target Server Version : 100411
 File Encoding         : 65001

 Date: 30/01/2021 19:03:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for filter
-- ----------------------------
DROP TABLE IF EXISTS `filter`;
CREATE TABLE `filter`  (
  `filterid` int(11) NOT NULL AUTO_INCREMENT COMMENT '滤波器主键',
  `filtermethod` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '滤波方法：mvav(移动平均)和fodl(一阶滤波),nofilt无滤波',
  `filteralphe` double NULL DEFAULT NULL COMMENT '一阶滤波系数',
  `filtercapacity` int(11) NULL DEFAULT NULL COMMENT '移动平均滤波时间',
  `refmodleid` int(11) NULL DEFAULT NULL COMMENT '模型id主键',
  PRIMARY KEY (`filterid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of filter
-- ----------------------------
INSERT INTO `filter` VALUES (1, 'fodl', 0.05, 20, 3);
INSERT INTO `filter` VALUES (3, 'fodl', 0.02, NULL, 14);
INSERT INTO `filter` VALUES (4, 'fodl', 0.1, NULL, 18);
INSERT INTO `filter` VALUES (5, 'fodl', 0.1, NULL, 36);
INSERT INTO `filter` VALUES (6, 'fodl', 0.4, NULL, 52);
INSERT INTO `filter` VALUES (7, 'fodl', 0.1, NULL, 55);

-- ----------------------------
-- Table structure for modle
-- ----------------------------
DROP TABLE IF EXISTS `modle`;
CREATE TABLE `modle`  (
  `modleId` int(11) NOT NULL AUTO_INCREMENT,
  `modleName` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `controlAPCOutCycle` int(11) NULL DEFAULT NULL COMMENT 'mpc 参数',
  `predicttime_P` int(11) NULL DEFAULT NULL COMMENT 'mpc 参数',
  `timeserise_N` int(11) NULL DEFAULT NULL COMMENT 'mpc 参数',
  `controltime_M` int(11) NULL DEFAULT NULL COMMENT 'mpc 参数',
  `modleEnable` int(11) NULL DEFAULT NULL COMMENT '模块使能',
  `runstyle` int(11) NULL DEFAULT 0 COMMENT 'mpc运行方式0-自动分配模式 1-手动分配模式',
  `modletype` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'input/output/filter/mpc/pid',
  `customizepyname` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自定义脚本名称',
  `refprojectid` int(11) NULL DEFAULT NULL COMMENT '引用projectid',
  PRIMARY KEY (`modleId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '控制模型' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modle
-- ----------------------------
INSERT INTO `modle` VALUES (1, '输入集合', NULL, NULL, NULL, NULL, 1, 0, 'input', NULL, 1);
INSERT INTO `modle` VALUES (2, '输出集合', NULL, NULL, NULL, NULL, 1, 0, 'output', NULL, 1);
INSERT INTO `modle` VALUES (3, '滤波器', NULL, NULL, NULL, NULL, 1, 0, 'filter', NULL, 1);
INSERT INTO `modle` VALUES (4, 'PID', NULL, NULL, NULL, NULL, 1, 0, 'pid', NULL, 1);
INSERT INTO `modle` VALUES (8, 'PID', NULL, NULL, NULL, NULL, 1, 0, 'pid', NULL, 1);
INSERT INTO `modle` VALUES (9, 'MPC', NULL, 80, 100, 2, 1, 0, 'mpc', NULL, 1);
INSERT INTO `modle` VALUES (11, '输入集合', NULL, NULL, NULL, NULL, 1, 0, 'input', NULL, 2);
INSERT INTO `modle` VALUES (12, '输出集合', NULL, NULL, NULL, NULL, 1, 0, 'output', NULL, 2);
INSERT INTO `modle` VALUES (13, 'MPC', NULL, 100, 120, 2, 1, 0, 'mpc', NULL, 2);
INSERT INTO `modle` VALUES (14, '滤波器', NULL, NULL, NULL, NULL, 1, 0, 'filter', NULL, 2);
INSERT INTO `modle` VALUES (15, '输入集合', NULL, NULL, NULL, NULL, 1, 0, 'input', NULL, 3);
INSERT INTO `modle` VALUES (16, '输出集合', NULL, NULL, NULL, NULL, 1, 0, 'output', NULL, 3);
INSERT INTO `modle` VALUES (17, 'MPC', NULL, 65, 80, 2, 1, 0, 'mpc', NULL, 3);
INSERT INTO `modle` VALUES (18, '滤波器', NULL, NULL, NULL, NULL, 1, 0, 'filter', NULL, 3);
INSERT INTO `modle` VALUES (19, '输入集合', NULL, NULL, NULL, NULL, 1, 0, 'input', NULL, 9);
INSERT INTO `modle` VALUES (22, '输出集合', NULL, NULL, NULL, NULL, 1, 0, 'output', NULL, 9);
INSERT INTO `modle` VALUES (24, '输入集合', NULL, NULL, NULL, NULL, 1, 0, 'input', NULL, 4);
INSERT INTO `modle` VALUES (25, '输出集合', NULL, NULL, NULL, NULL, 1, 0, 'output', NULL, 4);
INSERT INTO `modle` VALUES (26, 'MPC1', NULL, 70, 90, 2, 1, 1, 'mpc', NULL, 4);
INSERT INTO `modle` VALUES (30, 'MPC2', NULL, 70, 90, 2, 1, 1, 'mpc', NULL, 4);
INSERT INTO `modle` VALUES (33, '输入集合', NULL, NULL, NULL, NULL, 1, 0, 'input', NULL, 7);
INSERT INTO `modle` VALUES (34, '输出集合', NULL, NULL, NULL, NULL, 1, 0, 'output', NULL, 7);
INSERT INTO `modle` VALUES (35, 'MPC1', NULL, 85, 120, 2, 1, 1, 'mpc', NULL, 7);
INSERT INTO `modle` VALUES (36, '滤波器', NULL, NULL, NULL, NULL, 1, 0, 'filter', NULL, 7);
INSERT INTO `modle` VALUES (37, '自定义', NULL, NULL, NULL, NULL, 1, 0, 'customize', '37_1611832697153.py', 9);
INSERT INTO `modle` VALUES (38, '输入集合', NULL, NULL, NULL, NULL, 1, 0, 'input', NULL, 8);
INSERT INTO `modle` VALUES (39, '输出集合', NULL, NULL, NULL, NULL, 1, 0, 'output', NULL, 8);
INSERT INTO `modle` VALUES (40, '输入集合', NULL, NULL, NULL, NULL, 1, 0, 'input', NULL, 6);
INSERT INTO `modle` VALUES (41, '输出集合', NULL, NULL, NULL, NULL, 1, 0, 'output', NULL, 6);
INSERT INTO `modle` VALUES (43, 'PID', NULL, NULL, NULL, NULL, 1, 0, 'pid', NULL, 6);
INSERT INTO `modle` VALUES (44, '输入集合', NULL, NULL, NULL, NULL, 1, 0, 'input', NULL, 5);
INSERT INTO `modle` VALUES (45, '输出集合', NULL, NULL, NULL, NULL, 1, 0, 'output', NULL, 5);
INSERT INTO `modle` VALUES (46, 'MPC', NULL, 60, 80, 2, 1, 1, 'mpc', NULL, 5);
INSERT INTO `modle` VALUES (47, 'MPC', NULL, NULL, NULL, NULL, 1, 0, 'mpc', NULL, 8);
INSERT INTO `modle` VALUES (50, 'MPC', NULL, 60, 70, 2, 1, 0, 'mpc', NULL, 7);
INSERT INTO `modle` VALUES (51, '二段跟随调节', NULL, NULL, NULL, NULL, 1, 0, 'customize', '51_1611914992535.py', 1);
INSERT INTO `modle` VALUES (52, '滤波器', NULL, NULL, NULL, NULL, 1, 0, 'filter', NULL, 6);
INSERT INTO `modle` VALUES (53, '输入集合', NULL, NULL, NULL, NULL, 1, 0, 'input', NULL, 10);
INSERT INTO `modle` VALUES (54, '输出集合', NULL, NULL, NULL, NULL, 1, 0, 'output', NULL, 10);
INSERT INTO `modle` VALUES (55, '滤波器', NULL, NULL, NULL, NULL, 1, 0, 'filter', NULL, 10);
INSERT INTO `modle` VALUES (56, 'MPC', NULL, 85, 120, 2, 1, 1, 'mpc', NULL, 10);
INSERT INTO `modle` VALUES (57, '输入集合', NULL, NULL, NULL, NULL, 1, 0, 'input', NULL, 11);
INSERT INTO `modle` VALUES (58, 'MPC', NULL, NULL, NULL, NULL, 1, 0, 'mpc', NULL, 11);
INSERT INTO `modle` VALUES (59, '输出集合', NULL, NULL, NULL, NULL, 1, 0, 'output', NULL, 11);

-- ----------------------------
-- Table structure for modlepins
-- ----------------------------
DROP TABLE IF EXISTS `modlepins`;
CREATE TABLE `modlepins`  (
  `modlepinsId` int(11) NOT NULL AUTO_INCREMENT,
  `refmodleId` int(11) NULL DEFAULT NULL,
  `modleOpcTag` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '\'NULL\'' COMMENT '数据源引脚(opc位号)',
  `modlePinName` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '\'NULL\'' COMMENT '引脚名称 pv1...',
  `opcTagName` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'NULL' COMMENT '数据源位号中文名称',
  `resource` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据源',
  `Q` double NULL DEFAULT NULL,
  `dmvHigh` double NULL DEFAULT NULL,
  `deadZone` double NULL DEFAULT NULL,
  `funelinitValue` double NULL DEFAULT NULL,
  `R` double NULL DEFAULT NULL,
  `dmvLow` double NULL DEFAULT NULL,
  `referTrajectoryCoef` double NULL DEFAULT 0 COMMENT '参考轨迹系数（柔化系数）',
  `funneltype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `pinEnable` int(11) NULL DEFAULT 1 COMMENT '引脚使能位，一般用于pv是否启用',
  `updateTime` timestamp(0) NULL DEFAULT current_timestamp(),
  `pintype` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'pv sp ff mv pvenable....',
  `tracoefmethod` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '柔化系数方法：\r\nbefore\r\nafter',
  `pindir` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '引脚方向，输入(input)还是输出(output)',
  `modlepropertyclazz` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'baseproperty/mpcproperty',
  PRIMARY KEY (`modlepinsId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 348 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modlepins
-- ----------------------------
INSERT INTO `modlepins` VALUES (1, 1, 'AI5720P03', 'AI5720P03', 'AI5720P03', '{\"resource\":\"opc\",\"inmappingtag\":\"AI5720P03\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 09:59:16', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (2, 1, 'AI5720P04', 'AI5720P04', 'AI5720P04', '{\"resource\":\"opc\",\"inmappingtag\":\"AI5720P04\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 09:59:26', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (3, 1, 'A_BLJ_SP', 'A_BLJ_SP', 'A_BLJ_SP', '{\"resource\":\"opc\",\"inmappingtag\":\"A_BLJ_SP\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 09:59:53', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (4, 1, 'A_BLJ_AM', 'A_BLJ_AM', 'A_BLJ_AM', '{\"resource\":\"opc\",\"inmappingtag\":\"A_BLJ_AM\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 10:00:00', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (5, 1, 'A_BLJ_HH', 'A_BLJ_HH', 'A_BLJ_HH', '{\"resource\":\"opc\",\"inmappingtag\":\"A_BLJ_HH\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 10:00:10', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (6, 1, 'A_BLJ_LL', 'A_BLJ_LL', 'A_BLJ_LL', '{\"resource\":\"opc\",\"inmappingtag\":\"A_BLJ_LL\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 10:00:17', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (7, 1, 'A_BLJ_MV', 'A_BLJ_MV', 'A_BLJ_MV', '{\"resource\":\"opc\",\"inmappingtag\":\"A_BLJ_MV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 10:00:26', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (10, 3, 'AI5720P03', 'AI5720P03LB', 'AI5720P03', '{\"modleId\":3,\"resource\":\"memory\",\"modlepinsId\":22}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 10:46:51', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (11, 4, 'mv', 'mv', 'PIDmv输出', '{\"resource\":\"memory\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 10:04:01', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (12, 4, '-0.0011', 'kp', '', '{\"resource\":\"constant\",\"value\":-0.0011}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:48:24', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (13, 4, '0.0', 'ki', '', '{\"resource\":\"constant\",\"value\":0.0}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:48:24', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (14, 4, '0.0', 'kd', '', '{\"resource\":\"constant\",\"value\":0.0}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:48:24', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (15, 4, '10_AI5720P03LB', 'pv', 'AI5720P03LB(AI5720P03)\n                                        ', '{\"modleId\":3,\"resource\":\"modle\",\"modlepinsId\":10}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 14:48:24', NULL, NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (16, 4, '3_A_BLJ_SP', 'sp', 'A_BLJ_SP(A_BLJ_SP)\n                                        ', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":3}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:48:24', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (17, 4, '23_N0211_WLL2', 'mv', 'N0211_WLL2(N0211_WLL2(物料量2))\n                                        ', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":23}', 0, 0.05, 0, 0, 0, 0.001, 0, NULL, 1, '2021-01-30 14:48:24', NULL, NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (18, 4, '5_A_BLJ_HH', 'mvup', '5_A_BLJ_HH', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":5}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:48:24', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (19, 4, '6_A_BLJ_LL', 'mvdown', '6_A_BLJ_LL', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":6}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:48:24', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (20, 2, 'mv', 'mv', 'mv(PIDmv输出)', '{\"modleId\":4,\"resource\":\"modle\",\"modlepinsId\":11,\"outputpinmappingtagname\":\"N0211_WLL2(物料量2)\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 16:16:41', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (21, 2, 'N0211_WLL2', 'N0211_WLL2', 'N0211_WLL2(物料量2)', '{\"resource\":\"opc\",\"modlepinsId\":20,\"outmappingtag\":\"N0211_WLL2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 16:16:41', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (22, 3, 'AI5720P03', 'AI5720P03', 'AI5720P03', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":1}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 10:06:08', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (23, 1, 'N0211_WLL2', 'N0211_WLL2', 'N0211_WLL2(物料量2)', '{\"resource\":\"opc\",\"inmappingtag\":\"N0211_WLL2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 16:17:33', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (25, 1, 'N0211_CLLX', 'N0211_CLLX', 'N0211_CLLX(石破视频识别车辆类型)', '{\"resource\":\"opc\",\"inmappingtag\":\"N0211_CLLX\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 18:52:01', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (26, 8, 'mv', 'mv', 'PID2mv', '{\"resource\":\"memory\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 18:54:09', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (27, 8, '-7.8E-4', 'kp', '', '{\"resource\":\"constant\",\"value\":-7.8E-4}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:48:05', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (28, 8, '-8.0E-7', 'ki', '', '{\"resource\":\"constant\",\"value\":-8.0E-7}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:48:05', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (29, 8, '0.0', 'kd', '', '{\"resource\":\"constant\",\"value\":0.0}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:48:05', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (30, 8, '10_AI5720P03LB', 'pv', 'AI5720P03LB(AI5720P03)\n                                        ', '{\"modleId\":3,\"resource\":\"modle\",\"modlepinsId\":10}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 14:48:05', NULL, NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (31, 8, '56_A_BLJ_SPLB', 'sp', 'A_BLJ_SPLB(A_BLJ_SP(A_BLJ_SP(A_BLJ_SP)))\n                                        ', '{\"modleId\":3,\"resource\":\"modle\",\"modlepinsId\":56}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:48:05', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (32, 8, '7_A_BLJ_MV', 'mv', 'A_BLJ_MV(A_BLJ_MV)\n                                        ', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":7}', 0, 0.05, 0, 0, 0, 0.001, 0, NULL, 1, '2021-01-30 14:48:05', NULL, NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (33, 8, '5_A_BLJ_HH', 'mvup', '5_A_BLJ_HH', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":5}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:48:05', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (34, 8, '6_A_BLJ_LL', 'mvdown', '6_A_BLJ_LL', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":6}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:48:05', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (35, 2, 'mv', 'mv', 'mv(PID2mv)', '{\"modleId\":8,\"resource\":\"modle\",\"modlepinsId\":26,\"outputpinmappingtagname\":\"A_BLJ_MV(A_BLJ_MV)\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 08:41:35', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (36, 2, 'A_BLJ_MV', 'A_BLJ_MV', 'A_BLJ_MV(A_BLJ_MV)', '{\"resource\":\"opc\",\"modlepinsId\":35,\"outmappingtag\":\"A_BLJ_MV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 08:41:35', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (37, 8, '4_A_BLJ_AM', 'auto', 'A_BLJ_AM(A_BLJ_AM)\n                                        ', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":4}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:48:05', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (38, 4, '1.0', 'auto', '', '{\"resource\":\"constant\",\"value\":1.0}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:48:24', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (39, 9, 'AI5720P03LB', 'pv1', 'AI5720P03LB(AI5720P03)', '{\"modleId\":3,\"resource\":\"modle\",\"modlepinsId\":10}', 0.0001, 0, 30, 30, 0, 0, 0.4, 'fullfunnel', 1, '2021-01-27 22:31:20', 'pv', 'before', 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (40, 9, '', 'pvup1', '', '{\"resource\":\"constant\",\"value\":10000.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-27 22:31:20', 'pvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (41, 9, '', 'pvdown1', '', '{\"resource\":\"constant\",\"value\":6000.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-27 22:31:20', 'pvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (42, 9, 'A_BLJ_SP', 'sp1', 'A_BLJ_SP(A_BLJ_SP)', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":3}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-27 22:31:20', 'sp', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (43, 9, 'N0211_TX1', 'mv1', 'N0211_TX1(N0211_TX1(通讯校验))', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":52}', 0, 0.1, 0, 0, 60, 0.01, 0, NULL, 1, '2021-01-27 22:59:55', 'mv', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (44, 9, 'A_BLJ_HH', 'mvup1', 'A_BLJ_HH(A_BLJ_HH)', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":5}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-27 22:59:55', 'mvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (45, 9, 'A_BLJ_LL', 'mvdown1', 'A_BLJ_LL(A_BLJ_LL)', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":6}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-27 22:59:55', 'mvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (46, 9, 'N0211_TX1', 'mvfb1', 'N0211_TX1(N0211_TX1(通讯校验))', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":52}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-27 22:59:55', 'mvfb', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (47, 9, 'mv1', 'mv1', 'MPCmv1', '{\"resource\":\"memory\"}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-27 22:34:12', NULL, NULL, 'output', 'mpcproperty');
INSERT INTO `modlepins` VALUES (48, 9, 'dmv1', 'dmv1', 'MPCdmv1', '{\"resource\":\"memory\"}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-27 22:34:35', NULL, NULL, 'output', 'mpcproperty');
INSERT INTO `modlepins` VALUES (49, 2, 'mv1', 'mv1', 'mv1(MPCmv1)', '{\"modleId\":9,\"resource\":\"modle\",\"modlepinsId\":47,\"outputpinmappingtagname\":\"N0211_TX1(通讯校验)\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 23:00:51', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (50, 2, 'N0211_TX1', 'N0211_TX1', 'N0211_TX1(通讯校验)', '{\"resource\":\"opc\",\"modlepinsId\":49,\"outmappingtag\":\"N0211_TX1\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 23:00:51', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (51, 1, 'N0211_TSSC', 'N0211_TSSC', 'N0211_TSSC(调试输出)', '{\"resource\":\"opc\",\"inmappingtag\":\"N0211_TSSC\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 22:41:34', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (52, 1, 'N0211_TX1', 'N0211_TX1', 'N0211_TX1(通讯校验)', '{\"resource\":\"opc\",\"inmappingtag\":\"N0211_TX1\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-27 22:59:25', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (55, 3, 'A_BLJ_SP', 'A_BLJ_SP', 'A_BLJ_SP(A_BLJ_SP)', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":3}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 14:41:03', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (56, 3, 'A_BLJ_SP', 'A_BLJ_SPLB', 'A_BLJ_SP(A_BLJ_SP(A_BLJ_SP))', '{\"modleId\":3,\"resource\":\"memory\",\"modlepinsId\":55}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 14:41:38', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (57, 9, 'A_BLJ_AM', 'auto', 'A_BLJ_AM(A_BLJ_AM)\n                                            ', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":4}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 15:53:12', 'auto', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (58, 11, 'A_FJL_PV', 'A_FJL_PV', 'A_FJL_PV(A_FJL_PV)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_FJL_PV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:21:26', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (59, 11, 'A_FJL_SP', 'A_FJL_SP', 'A_FJL_SP(A_FJL_SP)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_FJL_SP\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:21:34', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (60, 11, 'A_FJL_AM', 'A_FJL_AM', 'A_FJL_AM(A_FJL_AM)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_FJL_AM\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:21:46', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (61, 11, 'A_FJL_HH', 'A_FJL_HH', 'A_FJL_HH(A_FJL_HH)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_FJL_HH\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:21:50', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (62, 11, 'A_FJL_LL', 'A_FJL_LL', 'A_FJL_LL(A_FJL_LL)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_FJL_LL\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:21:56', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (63, 11, 'A_FJL_MV', 'A_FJL_MV', 'A_FJL_MV(A_FJL_MV)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_FJL_MV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:22:01', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (64, 11, 'AI7503P01', 'AI7503P01', 'AI7503P01(7503出口压力)', '{\"resource\":\"opc\",\"inmappingtag\":\"AI7503P01\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:23:03', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (65, 13, '240_AI51C5', 'pv1', 'AI51C5(AI51C5(AI51C5))', '{\"modleId\":11,\"resource\":\"modle\",\"modlepinsId\":240}', 0.0004, 0, 0.4, 0.5, 0, 0, 0.66, 'fullfunnel', 1, '2021-01-29 13:09:42', 'pv', 'before', 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (66, 13, '', 'pvup1', '', '{\"resource\":\"constant\",\"value\":1000.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-29 13:09:42', 'pvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (67, 13, '', 'pvdown1', '', '{\"resource\":\"constant\",\"value\":600.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-29 13:09:42', 'pvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (68, 13, '59_A_FJL_SP', 'sp1', 'A_FJL_SP(A_FJL_SP(A_FJL_SP))', '{\"modleId\":11,\"resource\":\"modle\",\"modlepinsId\":59}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-29 13:09:42', 'sp', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (69, 13, 'A_FJL_MV', 'mv1', 'A_FJL_MV(A_FJL_MV(A_FJL_MV))', '{\"modleId\":11,\"resource\":\"modle\",\"modlepinsId\":63}', 0, 0.09, 0, 0, 20, 0.01, 0, NULL, 1, '2021-01-28 18:26:30', 'mv', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (70, 13, 'A_FJL_HH', 'mvup1', 'A_FJL_HH(A_FJL_HH(A_FJL_HH))', '{\"modleId\":11,\"resource\":\"modle\",\"modlepinsId\":61}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:26:30', 'mvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (71, 13, 'A_FJL_LL', 'mvdown1', 'A_FJL_LL(A_FJL_LL(A_FJL_LL))', '{\"modleId\":11,\"resource\":\"modle\",\"modlepinsId\":62}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:26:30', 'mvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (72, 13, 'A_FJL_MV', 'mvfb1', 'A_FJL_MV(A_FJL_MV(A_FJL_MV))', '{\"modleId\":11,\"resource\":\"modle\",\"modlepinsId\":63}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:26:30', 'mvfb', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (73, 13, 'mv1', 'mv1', 'MPCMV1', '{\"resource\":\"memory\"}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:27:47', NULL, NULL, 'output', 'mpcproperty');
INSERT INTO `modlepins` VALUES (74, 13, '60_A_FJL_AM', 'auto', 'A_FJL_AM(A_FJL_AM(A_FJL_AM))\n                                            ', '{\"modleId\":11,\"resource\":\"modle\",\"modlepinsId\":60}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 11:14:50', 'auto', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (75, 13, 'dmv1', 'dmv1', 'MPCDMV1', '{\"resource\":\"memory\"}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:28:16', NULL, NULL, 'output', 'mpcproperty');
INSERT INTO `modlepins` VALUES (76, 12, 'mv1', 'mv1', 'mv1(MPCMV1)', '{\"modleId\":13,\"resource\":\"modle\",\"modlepinsId\":73,\"outputpinmappingtagname\":\"A_FJL_MV(A_FJL_MV)\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:28:34', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (77, 12, 'A_FJL_MV', 'A_FJL_MV', 'A_FJL_MV(A_FJL_MV)', '{\"resource\":\"opc\",\"modlepinsId\":76,\"outmappingtag\":\"A_FJL_MV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:28:34', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (79, 15, 'A_YTP_SP', 'A_YTP_SP', 'A_YTP_SP(A_YTP_SP)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_YTP_SP\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:30:39', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (80, 15, 'A_YTP_AM', 'A_YTP_AM', 'A_YTP_AM(A_YTP_AM)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_YTP_AM\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:30:45', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (81, 15, 'A_YTP_HH', 'A_YTP_HH', 'A_YTP_HH(A_YTP_HH)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_YTP_HH\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:30:50', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (82, 15, 'A_YTP_LL', 'A_YTP_LL', 'A_YTP_LL(A_YTP_LL)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_YTP_LL\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:30:53', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (83, 15, 'A_YTP_MV', 'A_YTP_MV', 'A_YTP_MV(A_YTP_MV)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_YTP_MV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:30:56', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (84, 17, '132_AI5701P01', 'pv1', 'AI5701P01(AI5701P01(AI5701P01(AI5701P01(窑头罩负压))))', '{\"modleId\":18,\"resource\":\"modle\",\"modlepinsId\":132}', 0.002, 0, 5, 5, 0, 0, 0.7, 'fullfunnel', 1, '2021-01-29 13:10:31', 'pv', 'after', 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (85, 17, '', 'pvup1', '', '{\"resource\":\"constant\",\"value\":200.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-29 13:10:31', 'pvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (86, 17, '', 'pvdown1', '', '{\"resource\":\"constant\",\"value\":-300.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-29 13:10:31', 'pvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (87, 17, '79_A_YTP_SP', 'sp1', 'A_YTP_SP(A_YTP_SP(A_YTP_SP))', '{\"modleId\":15,\"resource\":\"modle\",\"modlepinsId\":79}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-29 13:10:31', 'sp', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (88, 17, '83_A_YTP_MV', 'mv1', 'A_YTP_MV(A_YTP_MV(A_YTP_MV))', '{\"modleId\":15,\"resource\":\"modle\",\"modlepinsId\":83}', 0, 2, 0, 0, 10, 0.2, 0, NULL, 1, '2021-01-29 13:14:23', 'mv', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (89, 17, '81_A_YTP_HH', 'mvup1', 'A_YTP_HH(A_YTP_HH(A_YTP_HH))', '{\"modleId\":15,\"resource\":\"modle\",\"modlepinsId\":81}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-29 13:14:23', 'mvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (90, 17, '82_A_YTP_LL', 'mvdown1', 'A_YTP_LL(A_YTP_LL(A_YTP_LL))', '{\"modleId\":15,\"resource\":\"modle\",\"modlepinsId\":82}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-29 13:14:23', 'mvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (91, 17, '82_A_YTP_LL', 'mvfb1', 'A_YTP_LL(A_YTP_LL(A_YTP_LL))', '{\"modleId\":15,\"resource\":\"modle\",\"modlepinsId\":82}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-29 13:14:23', 'mvfb', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (92, 17, 'mv1', 'mv1', 'MPCMV1', '{\"resource\":\"memory\"}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:33:54', NULL, NULL, 'output', 'mpcproperty');
INSERT INTO `modlepins` VALUES (93, 17, 'dmv1', 'dmv1', 'MPVDMV1', '{\"resource\":\"memory\"}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:34:06', NULL, NULL, 'output', 'mpcproperty');
INSERT INTO `modlepins` VALUES (94, 17, '80_A_YTP_AM', 'auto', 'A_YTP_AM(A_YTP_AM(A_YTP_AM))\n                                            ', '{\"modleId\":15,\"resource\":\"modle\",\"modlepinsId\":80}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-29 14:20:02', 'auto', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (95, 16, 'mv1', 'mv1', 'mv1(MPCMV1)', '{\"modleId\":17,\"resource\":\"modle\",\"modlepinsId\":92,\"outputpinmappingtagname\":\"A_YTP_MV(A_YTP_MV)\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:34:27', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (96, 16, 'A_YTP_MV', 'A_YTP_MV', 'A_YTP_MV(A_YTP_MV)', '{\"resource\":\"opc\",\"modlepinsId\":95,\"outmappingtag\":\"A_YTP_MV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:34:27', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (97, 24, 'A_MMT_AM', 'A_MMT_AM', 'A_MMT_AM(A_MMT_AM)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_MMT_AM\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:40:39', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (98, 24, 'A_MMT_AM2', 'A_MMT_AM2', 'A_MMT_AM2(A_MMT_AM2)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_MMT_AM2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:40:42', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (99, 24, 'A_MMT_HH', 'A_MMT_HH', 'A_MMT_HH(A_MMT_HH)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_MMT_HH\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:40:47', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (100, 24, 'A_MMT_HH2', 'A_MMT_HH2', 'A_MMT_HH2(A_MMT_HH2)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_MMT_HH2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:40:51', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (101, 24, 'A_MMT_LL', 'A_MMT_LL', 'A_MMT_LL(A_MMT_LL)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_MMT_LL\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:40:54', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (102, 24, 'A_MMT_LL2', 'A_MMT_LL2', 'A_MMT_LL2(A_MMT_LL2)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_MMT_LL2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:40:59', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (103, 24, 'A_MMT_MV', 'A_MMT_MV', 'A_MMT_MV(A_MMT_MV)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_MMT_MV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:41:03', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (104, 24, 'A_MMT_MV2', 'A_MMT_MV2', 'A_MMT_MV2(A_MMT_MV2)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_MMT_MV2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:41:07', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (105, 24, 'A_MMT_SP', 'A_MMT_SP', 'A_MMT_SP(A_MMT_SP)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_MMT_SP\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:41:14', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (106, 24, 'A_MMT_SP2', 'A_MMT_SP2', 'A_MMT_SP2(A_MMT_SP2)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_MMT_SP2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:41:20', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (107, 24, 'AI7306T01', 'AI7306T01', 'AI7306T01(煤磨入口温度)', '{\"resource\":\"opc\",\"inmappingtag\":\"AI7306T01\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:42:04', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (108, 24, 'AI7306T02', 'AI7306T02', 'AI7306T02(煤磨出口温度)', '{\"resource\":\"opc\",\"inmappingtag\":\"AI7306T02\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:42:08', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (109, 26, 'AI7306T01', 'pv1', 'AI7306T01(AI7306T01(煤磨入口温度))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":107}', 0.25, 0, 4, 2, 0, 0, 0.6, 'fullfunnel', 1, '2021-01-28 18:45:17', 'pv', 'after', 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (110, 26, '', 'pvup1', '', '{\"resource\":\"constant\",\"value\":300.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:45:17', 'pvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (111, 26, '', 'pvdown1', '', '{\"resource\":\"constant\",\"value\":100.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:45:17', 'pvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (112, 26, 'A_MMT_SP', 'sp1', 'A_MMT_SP(A_MMT_SP(A_MMT_SP))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":105}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:45:17', 'sp', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (113, 26, 'A_MMT_MV', 'mv1', 'A_MMT_MV(A_MMT_MV(A_MMT_MV))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":103}', 0, 3, 0, 0, 2, 0.5, 0, NULL, 1, '2021-01-28 18:46:39', 'mv', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (114, 26, 'A_MMT_HH', 'mvup1', 'A_MMT_HH(A_MMT_HH(A_MMT_HH))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":99}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:46:39', 'mvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (115, 26, 'A_MMT_LL', 'mvdown1', 'A_MMT_LL(A_MMT_LL(A_MMT_LL))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":101}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:46:39', 'mvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (116, 26, 'A_MMT_MV', 'mvfb1', 'A_MMT_MV(A_MMT_MV(A_MMT_MV))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":103}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:46:39', 'mvfb', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (117, 26, 'mv1', 'mv1', 'MPC1MV1', '{\"resource\":\"memory\"}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:26:15', NULL, NULL, 'output', 'mpcproperty');
INSERT INTO `modlepins` VALUES (118, 26, 'A_MMT_AM', 'auto', 'A_MMT_AM(A_MMT_AM(A_MMT_AM))\n                                            ', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":97}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:27:57', 'auto', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (119, 26, 'AI7306T02', 'pv2', 'AI7306T02(AI7306T02(煤磨出口温度))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":108}', 0.8, 0, 0.3, 0.3, 0, 0, 0.7, 'fullfunnel', 1, '2021-01-28 18:49:12', 'pv', 'before', 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (120, 26, '', 'pvup2', '', '{\"resource\":\"constant\",\"value\":80.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:49:12', 'pvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (121, 26, '', 'pvdown2', '', '{\"resource\":\"constant\",\"value\":40.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:49:12', 'pvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (122, 26, 'A_MMT_SP2', 'sp2', 'A_MMT_SP2(A_MMT_SP2(A_MMT_SP2))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":106}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:49:12', 'sp', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (123, 26, 'A_MMT_MV2', 'mv2', 'A_MMT_MV2(A_MMT_MV2(A_MMT_MV2))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":104}', 0, 2, 0, 0, 5, 0.4, 0, NULL, 1, '2021-01-28 18:50:10', 'mv', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (124, 26, 'A_MMT_HH2', 'mvup2', 'A_MMT_HH2(A_MMT_HH2(A_MMT_HH2))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":100}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:50:10', 'mvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (125, 26, 'A_MMT_LL2', 'mvdown2', 'A_MMT_LL2(A_MMT_LL2(A_MMT_LL2))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":102}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:50:10', 'mvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (126, 26, 'A_MMT_MV2', 'mvfb2', 'A_MMT_MV2(A_MMT_MV2(A_MMT_MV2))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":104}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 18:50:10', 'mvfb', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (127, 14, 'AI7503P01', 'AI7503P01', 'AI7503P01(AI7503P01(7503出口压力))', '{\"modleId\":11,\"resource\":\"modle\",\"modlepinsId\":64}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:55:37', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (128, 14, 'AI7503P01', 'AI7503P01LB', 'AI7503P01(AI7503P01(7503出口压力))', '{\"modleId\":14,\"resource\":\"memory\",\"modlepinsId\":127}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:55:47', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (131, 18, 'AI5701P01', 'AI5701P01', 'AI5701P01(AI5701P01(窑头罩负压))', '{\"modleId\":15,\"resource\":\"modle\",\"modlepinsId\":133}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:59:22', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (132, 18, 'AI5701P01', 'AI5701P01', 'AI5701P01(AI5701P01(AI5701P01(窑头罩负压)))', '{\"modleId\":18,\"resource\":\"memory\",\"modlepinsId\":131}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:59:28', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (133, 15, 'AI5701P01', 'AI5701P01', 'AI5701P01(窑头罩负压)', '{\"resource\":\"opc\",\"inmappingtag\":\"AI5701P01\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 18:59:03', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (134, 16, 'AI5701P01', 'AI5701P01', 'AI5701P01(AI5701P01(AI5701P01(AI5701P01(窑头罩负压))))', '{\"modleId\":18,\"resource\":\"modle\",\"modlepinsId\":132,\"outputpinmappingtagname\":\"A_YTP_PV(A_YTP_PV)\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:02:59', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (135, 16, 'A_YTP_PV', 'A_YTP_PV', 'A_YTP_PV(A_YTP_PV)', '{\"resource\":\"opc\",\"modlepinsId\":134,\"outmappingtag\":\"A_YTP_PV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:02:59', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (136, 30, 'A_MMT_AM2', 'auto', 'A_MMT_AM2(A_MMT_AM2(A_MMT_AM2))\n                                            ', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":98}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:28:53', 'auto', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (137, 30, 'AI7306T01', 'pv1', 'AI7306T01(AI7306T01(煤磨入口温度))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":107}', 0.2, 0, 5, 6, 0, 0, 0.6, 'fullfunnel', 1, '2021-01-28 19:06:25', 'pv', 'after', 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (138, 30, '', 'pvup1', '', '{\"resource\":\"constant\",\"value\":280.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:06:25', 'pvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (139, 30, '', 'pvdown1', '', '{\"resource\":\"constant\",\"value\":150.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:06:25', 'pvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (140, 30, 'A_MMT_SP', 'sp1', 'A_MMT_SP(A_MMT_SP(A_MMT_SP))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":105}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:06:25', 'sp', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (141, 33, 'A_SLM1_AM1', 'A_SLM1_AM1', 'A_SLM1_AM1(A_SLM1_AM1)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_AM1\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:11:26', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (142, 33, 'A_SLM1_HI1', 'A_SLM1_HI1', 'A_SLM1_HI1(A_SLM1_HI1)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_HI1\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:11:36', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (143, 33, 'A_SLM1_LO1', 'A_SLM1_LO1', 'A_SLM1_LO1(A_SLM1_LO1)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_LO1\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:11:42', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (144, 33, 'A_SLM1_MV1', 'A_SLM1_MV1', 'A_SLM1_MV1(A_SLM1_MV1)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_MV1\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:11:51', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (145, 33, 'A_SLM1_SV1', 'A_SLM1_SV1', 'A_SLM1_SV1(A_SLM1_SV1)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_SV1\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:12:09', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (146, 33, 'A_SLM1_SV2', 'A_SLM1_SV2', 'A_SLM1_SV2(A_SLM1_SV2)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_SV2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:12:41', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (147, 33, 'A_SLM1_SV3', 'A_SLM1_SV3', 'A_SLM1_SV3(A_SLM1_SV3)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_SV3\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:12:48', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (148, 33, 'A_SLM1_SV4', 'A_SLM1_SV4', 'A_SLM1_SV4(A_SLM1_SV4)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_SV4\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:12:54', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (149, 33, 'A_SLM1_SV5', 'A_SLM1_SV5', 'A_SLM1_SV5(A_SLM1_SV5)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_SV5\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:12:59', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (150, 33, 'AI4105P05', 'AI4105P05', 'AI4105P05(AI4105P05)', '{\"resource\":\"opc\",\"inmappingtag\":\"AI4105P05\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:13:50', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (151, 33, 'AI41054L', 'AI41054L', 'AI41054L(料层厚度 (pv))', '{\"resource\":\"opc\",\"inmappingtag\":\"AI41054L\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:14:17', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (152, 33, 'A41E1AH04I', 'A41E1AH04I', 'A41E1AH04I(原料磨主电机电流)', '{\"resource\":\"opc\",\"inmappingtag\":\"A41E1AH04I\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:14:30', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (153, 33, 'AI4111T01', 'AI4111T01', 'AI4111T01(AI4111T01)', '{\"resource\":\"opc\",\"inmappingtag\":\"AI4111T01\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:14:51', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (154, 35, '1.0', 'auto', '', '{\"resource\":\"constant\",\"value\":1.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 14:56:18', 'auto', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (155, 36, 'AI4105P05', 'AI4105P05', 'AI4105P05(AI4105P05(AI4105P05))', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":150}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:19:14', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (156, 36, 'AI4105P05', 'AI4105P05LB', 'AI4105P05(AI4105P05(AI4105P05))', '{\"modleId\":36,\"resource\":\"memory\",\"modlepinsId\":155}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:19:22', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (157, 36, 'AI41054L', 'AI41054L', 'AI41054L(AI41054L(料层厚度 (pv)))', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":151}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:19:35', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (158, 36, 'AI41054L', 'AI41054LLB', 'AI41054L(AI41054L(料层厚度 (pv)))', '{\"modleId\":36,\"resource\":\"memory\",\"modlepinsId\":157}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:19:42', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (159, 13, '128_AI7503P01LB', 'ff1', 'AI7503P01LB(AI7503P01(AI7503P01(7503出口压力)))', '{\"modleId\":14,\"resource\":\"modle\",\"modlepinsId\":128}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 11:11:25', 'ff', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (160, 13, '', 'ffup1', '', '{\"resource\":\"constant\",\"value\":28000.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 11:11:25', 'ffup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (161, 13, '', 'ffdown1', '', '{\"resource\":\"constant\",\"value\":16000.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 11:11:25', 'ffdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (162, 19, 'A_TXJY', 'A_TXJY', 'A_TXJY(平台通讯校验)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_TXJY\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:20:46', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (163, 37, 'A_TXJY', 'pin_txjy', 'A_TXJY(A_TXJY(平台通讯校验))', '{\"modleId\":19,\"resource\":\"modle\",\"modlepinsId\":162}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:21:26', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (164, 37, 'pin_out_txjy', 'pin_out_txjy', '通讯校验输出', '{\"resource\":\"memory\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:21:59', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (165, 30, 'A_MMT_MV2', 'mv1', 'A_MMT_MV2(A_MMT_MV2(A_MMT_MV2))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":104}', 0, 2, 0, 0, 5, 0.4, 0, NULL, 1, '2021-01-28 19:22:46', 'mv', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (166, 30, 'A_MMT_HH2', 'mvup1', 'A_MMT_HH2(A_MMT_HH2(A_MMT_HH2))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":100}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:22:46', 'mvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (167, 30, 'A_MMT_LL2', 'mvdown1', 'A_MMT_LL2(A_MMT_LL2(A_MMT_LL2))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":102}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:22:46', 'mvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (168, 30, 'A_MMT_MV2', 'mvfb1', 'A_MMT_MV2(A_MMT_MV2(A_MMT_MV2))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":104}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:22:46', 'mvfb', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (169, 22, 'pin_out_txjy', 'pin_out_txjy', 'pin_out_txjy(通讯校验输出)', '{\"modleId\":37,\"resource\":\"modle\",\"modlepinsId\":164,\"outputpinmappingtagname\":\"A_TXJY(平台通讯校验)\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:23:57', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (170, 22, 'A_TXJY', 'A_TXJY', 'A_TXJY(平台通讯校验)', '{\"resource\":\"opc\",\"modlepinsId\":169,\"outmappingtag\":\"A_TXJY\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:23:57', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (171, 30, 'AI7306T02', 'pv2', 'AI7306T02(AI7306T02(煤磨出口温度))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":108}', 2, 0, 0.9, 0.6, 0, 0, 0.6, 'downfunnel', 1, '2021-01-28 19:23:59', 'pv', 'before', 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (172, 30, '', 'pvup2', '', '{\"resource\":\"constant\",\"value\":80.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:23:59', 'pvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (173, 30, '', 'pvdown2', '', '{\"resource\":\"constant\",\"value\":30.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:23:59', 'pvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (174, 30, 'A_MMT_SP2', 'sp2', 'A_MMT_SP2(A_MMT_SP2(A_MMT_SP2))', '{\"modleId\":24,\"resource\":\"modle\",\"modlepinsId\":106}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:23:59', 'sp', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (175, 30, 'mv1', 'mv1', 'MPC2MV1', '{\"resource\":\"memory\"}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:27:50', NULL, NULL, 'output', 'mpcproperty');
INSERT INTO `modlepins` VALUES (176, 25, '117_mv1', '117_mv1', 'mv1(MPC1MV1)', '{\"modleId\":26,\"resource\":\"modle\",\"modlepinsId\":117,\"outputpinmappingtagname\":\"A_MMT_MV(A_MMT_MV)\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 20:25:00', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (177, 25, 'A_MMT_MV', 'A_MMT_MV', 'A_MMT_MV(A_MMT_MV)', '{\"resource\":\"opc\",\"modlepinsId\":176,\"outmappingtag\":\"A_MMT_MV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 20:25:00', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (178, 25, '175_mv1', '175_mv1', 'mv1(MPC2MV1)', '{\"modleId\":30,\"resource\":\"modle\",\"modlepinsId\":175,\"outputpinmappingtagname\":\"A_MMT_MV2(A_MMT_MV2)\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 20:25:05', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (179, 25, 'A_MMT_MV2', 'A_MMT_MV2', 'A_MMT_MV2(A_MMT_MV2)', '{\"resource\":\"opc\",\"modlepinsId\":178,\"outmappingtag\":\"A_MMT_MV2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 20:25:05', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (184, 33, 'AI4108I', 'AI4108I', 'AI4108I(提升机电流)', '{\"resource\":\"opc\",\"inmappingtag\":\"AI4108I\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:36:44', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (185, 36, 'AI4108I', 'AI4108I', 'AI4108I(AI4108I(提升机电流))', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":184}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:36:57', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (186, 36, 'AI4108I', 'AI4108ILB', 'AI4108I(AI4108I(提升机电流))', '{\"modleId\":36,\"resource\":\"memory\",\"modlepinsId\":185}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:37:06', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (187, 35, 'AI4108ILB', 'pv2', 'AI4108ILB(AI4108I(AI4108I(提升机电流)))', '{\"modleId\":36,\"resource\":\"modle\",\"modlepinsId\":186}', 1.2, 0, 2, 1, 0, 0, 0.6, 'fullfunnel', 1, '2021-01-28 19:38:08', 'pv', 'after', 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (188, 35, '', 'pvup2', '', '{\"resource\":\"constant\",\"value\":40.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:38:08', 'pvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (189, 35, '', 'pvdown2', '', '{\"resource\":\"constant\",\"value\":15.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:38:08', 'pvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (190, 35, 'A_SLM1_SV2', 'sp2', 'A_SLM1_SV2(A_SLM1_SV2(A_SLM1_SV2))', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":146}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:38:08', 'sp', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (191, 35, 'AI41054LLB', 'pv4', 'AI41054LLB(AI41054L(AI41054L(料层厚度 (pv))))', '{\"modleId\":36,\"resource\":\"modle\",\"modlepinsId\":158}', 0.2, 0, 2, 2, 0, 0, 0.6, 'fullfunnel', 1, '2021-01-28 19:39:11', 'pv', 'after', 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (192, 35, '', 'pvup4', '', '{\"resource\":\"constant\",\"value\":150.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:39:11', 'pvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (193, 35, '', 'pvdown4', '', '{\"resource\":\"constant\",\"value\":30.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:39:11', 'pvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (194, 35, 'A_SLM1_SV4', 'sp4', 'A_SLM1_SV4(A_SLM1_SV4(A_SLM1_SV4))', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":148}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:39:11', 'sp', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (195, 35, 'A41E1AH04I', 'pv5', 'A41E1AH04I(A41E1AH04I(原料磨主电机电流))', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":152}', 0.18, 0, 2, 1, 0, 0, 0.7, 'fullfunnel', 1, '2021-01-28 19:40:16', 'pv', 'after', 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (196, 35, '', 'pvup5', '', '{\"resource\":\"constant\",\"value\":200.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:40:16', 'pvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (197, 35, '', 'pvdown5', '', '{\"resource\":\"constant\",\"value\":100.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:40:16', 'pvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (198, 35, 'A_SLM1_SV5', 'sp5', 'A_SLM1_SV5(A_SLM1_SV5(A_SLM1_SV5))', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":149}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:40:16', 'sp', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (199, 35, '144_A_SLM1_MV1', 'mv1', 'A_SLM1_MV1(A_SLM1_MV1(A_SLM1_MV1))', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":144}', 0, 3, 0, 0, 40, 0.05, 0, NULL, 1, '2021-01-30 14:53:36', 'mv', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (200, 35, '142_A_SLM1_HI1', 'mvup1', 'A_SLM1_HI1(A_SLM1_HI1(A_SLM1_HI1))', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":142}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 14:53:36', 'mvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (201, 35, '143_A_SLM1_LO1', 'mvdown1', 'A_SLM1_LO1(A_SLM1_LO1(A_SLM1_LO1))', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":143}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 14:53:36', 'mvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (202, 35, '144_A_SLM1_MV1', 'mvfb1', 'A_SLM1_MV1(A_SLM1_MV1(A_SLM1_MV1))', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":144}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 14:53:36', 'mvfb', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (203, 35, 'mv1', 'mv1', 'MPCMV1', '{\"resource\":\"memory\"}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:44:17', NULL, NULL, 'output', 'mpcproperty');
INSERT INTO `modlepins` VALUES (204, 34, 'mv1', 'mv1', 'mv1(MPCMV1)', '{\"modleId\":35,\"resource\":\"modle\",\"modlepinsId\":203,\"outputpinmappingtagname\":\"A_SLM1_MV1(A_SLM1_MV1)\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:44:49', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (205, 34, 'A_SLM1_MV1', 'A_SLM1_MV1', 'A_SLM1_MV1(A_SLM1_MV1)', '{\"resource\":\"opc\",\"modlepinsId\":204,\"outmappingtag\":\"A_SLM1_MV1\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:44:49', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (206, 33, 'AI4105101S', 'AI4105101S', 'AI4105101S(选粉机变频速度反馈)', '{\"resource\":\"opc\",\"inmappingtag\":\"AI4105101S\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:46:08', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (207, 33, 'A41E1AH05I', 'A41E1AH05I', 'A41E1AH05I(循环风机主电机电流)', '{\"resource\":\"opc\",\"inmappingtag\":\"A41E1AH05I\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:46:44', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (208, 35, 'AI4105101S', 'ff2', 'AI4105101S(AI4105101S(选粉机变频速度反馈))', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":206}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:47:42', 'ff', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (209, 35, '', 'ffup2', '', '{\"resource\":\"constant\",\"value\":1300.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:47:42', 'ffup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (210, 35, '', 'ffdown2', '', '{\"resource\":\"constant\",\"value\":1000.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:47:42', 'ffdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (211, 36, 'A41E1AH05I', 'A41E1AH05I', 'A41E1AH05I(A41E1AH05I(循环风机主电机电流))', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":207}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:48:02', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (212, 36, 'A41E1AH05I', 'A41E1AH05ILB', 'A41E1AH05I(A41E1AH05I(循环风机主电机电流))', '{\"modleId\":36,\"resource\":\"memory\",\"modlepinsId\":211}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:48:10', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (213, 35, 'A41E1AH05ILB', 'ff1', 'A41E1AH05ILB(A41E1AH05I(A41E1AH05I(循环风机主电机电流)))', '{\"modleId\":36,\"resource\":\"modle\",\"modlepinsId\":212}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:48:39', 'ff', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (214, 35, '', 'ffup1', '', '{\"resource\":\"constant\",\"value\":200.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:48:39', 'ffup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (215, 35, '', 'ffdown1', '', '{\"resource\":\"constant\",\"value\":160.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:48:39', 'ffdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (216, 44, 'AI7306T02', 'AI7306T02', 'AI7306T02(煤磨出口温度)', '{\"resource\":\"opc\",\"inmappingtag\":\"AI7306T02\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:53:06', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (217, 44, 'A_MMF_AM', 'A_MMF_AM', 'A_MMF_AM(A_MMF_AM)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_MMF_AM\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:53:22', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (218, 44, 'A_MMF_HH', 'A_MMF_HH', 'A_MMF_HH(A_MMF_HH)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_MMF_HH\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:53:28', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (219, 44, 'A_MMF_LL', 'A_MMF_LL', 'A_MMF_LL(A_MMF_LL)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_MMF_LL\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:53:32', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (220, 44, 'A_MMF_MV', 'A_MMF_MV', 'A_MMF_MV(A_MMF_MV)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_MMF_MV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:53:37', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (221, 44, 'A_MMF_PV', 'A_MMF_PV', 'A_MMF_PV(A_MMF_PV)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_MMF_PV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:53:42', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (222, 46, 'AI7306T02', 'pv1', 'AI7306T02(AI7306T02(煤磨出口温度))', '{\"modleId\":44,\"resource\":\"modle\",\"modlepinsId\":216}', 0.4, 0, 1.2, 0.8, 0, 0, 0.7, 'fullfunnel', 1, '2021-01-28 19:55:43', 'pv', 'before', 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (223, 46, '', 'pvup1', '', '{\"resource\":\"constant\",\"value\":70.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:55:43', 'pvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (224, 46, '', 'pvdown1', '', '{\"resource\":\"constant\",\"value\":50.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:55:43', 'pvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (225, 46, 'A_MMT_SP2', 'sp1', 'A_MMT_SP2(A_MMT_SP2(A_MMT_SP2))', '{\"modleId\":44,\"resource\":\"modle\",\"modlepinsId\":227}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:55:43', 'sp', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (226, 46, 'A_MMF_AM', 'auto', 'A_MMF_AM(A_MMF_AM(A_MMF_AM))\n                                            ', '{\"modleId\":44,\"resource\":\"modle\",\"modlepinsId\":217}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:59:19', 'auto', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (227, 44, 'A_MMT_SP2', 'A_MMT_SP2', 'A_MMT_SP2(A_MMT_SP2)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_MMT_SP2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:55:28', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (228, 46, 'A_MMF_PV', 'pv2', 'A_MMF_PV(A_MMF_PV(A_MMF_PV))', '{\"modleId\":44,\"resource\":\"modle\",\"modlepinsId\":221}', 0.25, 0, 12, 5, 0, 0, 0.65, 'fullfunnel', 1, '2021-01-28 19:56:54', 'pv', 'before', 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (229, 46, '', 'pvup2', '', '{\"resource\":\"constant\",\"value\":50.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:56:54', 'pvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (230, 46, '', 'pvdown2', '', '{\"resource\":\"constant\",\"value\":-50.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:56:54', 'pvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (231, 46, '', 'sp2', '', '{\"resource\":\"constant\",\"value\":1.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:56:54', 'sp', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (232, 46, 'A_MMF_MV', 'mv1', 'A_MMF_MV(A_MMF_MV(A_MMF_MV))', '{\"modleId\":44,\"resource\":\"modle\",\"modlepinsId\":220}', 0, 0.2, 0, 0, 50, 0.1, 0, NULL, 1, '2021-01-28 19:57:45', 'mv', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (233, 46, 'A_MMF_HH', 'mvup1', 'A_MMF_HH(A_MMF_HH(A_MMF_HH))', '{\"modleId\":44,\"resource\":\"modle\",\"modlepinsId\":218}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:57:45', 'mvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (234, 46, 'A_MMF_LL', 'mvdown1', 'A_MMF_LL(A_MMF_LL(A_MMF_LL))', '{\"modleId\":44,\"resource\":\"modle\",\"modlepinsId\":219}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:57:45', 'mvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (235, 46, 'A_MMF_MV', 'mvfb1', 'A_MMF_MV(A_MMF_MV(A_MMF_MV))', '{\"modleId\":44,\"resource\":\"modle\",\"modlepinsId\":220}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:57:45', 'mvfb', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (236, 46, 'mv1', 'mv1', 'MPCmv1', '{\"resource\":\"memory\"}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-28 19:59:19', NULL, NULL, 'output', 'mpcproperty');
INSERT INTO `modlepins` VALUES (237, 45, 'mv1', 'mv1', 'mv1(MPCmv1)', '{\"modleId\":46,\"resource\":\"modle\",\"modlepinsId\":236,\"outputpinmappingtagname\":\"A_MMF_MV(A_MMF_MV)\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:59:36', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (238, 45, 'A_MMF_MV', 'A_MMF_MV', 'A_MMF_MV(A_MMF_MV)', '{\"resource\":\"opc\",\"modlepinsId\":237,\"outmappingtag\":\"A_MMF_MV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-28 19:59:36', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (239, 35, 'dpv2mv1', 'dpv2mv1', '手动分配增量', '{\"resource\":\"memory\"}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 14:54:58', NULL, NULL, 'output', 'mpcproperty');
INSERT INTO `modlepins` VALUES (240, 11, 'AI51C5', 'AI51C5', 'AI51C5(AI51C5)', '{\"resource\":\"opc\",\"inmappingtag\":\"AI51C5\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-29 13:09:25', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (241, 40, 'A_SLM1_AM2', 'A_SLM1_AM2', 'A_SLM1_AM2(A_SLM1_AM2)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_AM2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-29 17:09:35', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (242, 40, 'A_SLM1_HI2', 'A_SLM1_HI2', 'A_SLM1_HI2(A_SLM1_HI2)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_HI2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-29 17:10:00', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (243, 40, 'A_SLM1_LO2', 'A_SLM1_LO2', 'A_SLM1_LO2(A_SLM1_LO2)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_LO2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-29 17:10:09', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (244, 40, 'A_SLM1_MV2', 'A_SLM1_MV2', 'A_SLM1_MV2(A_SLM1_MV2)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_MV2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-29 17:10:21', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (245, 40, 'AI41059F01', 'AI41059F01', 'AI41059F01(立磨喷水流量)', '{\"resource\":\"opc\",\"inmappingtag\":\"AI41059F01\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-29 17:11:33', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (246, 43, 'mv', 'mv', 'PIDmv', '{\"resource\":\"memory\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-29 17:12:34', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (247, 43, '7.0', 'kp', '', '{\"resource\":\"constant\",\"value\":7.0}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:49:00', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (248, 43, '22.0', 'ki', '', '{\"resource\":\"constant\",\"value\":22.0}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:49:00', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (249, 43, '4.0', 'kd', '', '{\"resource\":\"constant\",\"value\":4.0}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:49:00', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (250, 43, '278_52_245_AI41059F01', 'pv', '52_245_AI41059F01(AI41059F01(AI41059F01(立磨喷水流量)))\n                                        ', '{\"modleId\":52,\"resource\":\"modle\",\"modlepinsId\":278}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 14:49:00', NULL, NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (251, 43, '274_A_SLM1_HSV', 'sp', 'A_SLM1_HSV(A_SLM1_HSV(流量自动设定))\n                                        ', '{\"modleId\":40,\"resource\":\"modle\",\"modlepinsId\":274}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:49:00', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (252, 43, '244_A_SLM1_MV2', 'mv', 'A_SLM1_MV2(A_SLM1_MV2(A_SLM1_MV2))\n                                        ', '{\"modleId\":40,\"resource\":\"modle\",\"modlepinsId\":244}', 0, 30, 0, 0, 0, 1, 0, NULL, 1, '2021-01-30 14:49:00', NULL, NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (253, 43, '242_A_SLM1_HI2', 'mvup', '242_A_SLM1_HI2', '{\"modleId\":40,\"resource\":\"modle\",\"modlepinsId\":242}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:49:00', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (254, 43, '243_A_SLM1_LO2', 'mvdown', '243_A_SLM1_LO2', '{\"modleId\":40,\"resource\":\"modle\",\"modlepinsId\":243}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:49:00', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (255, 43, '241_A_SLM1_AM2', 'auto', 'A_SLM1_AM2(A_SLM1_AM2(A_SLM1_AM2))\n                                        ', '{\"modleId\":40,\"resource\":\"modle\",\"modlepinsId\":241}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:49:00', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (256, 41, '246_mv', '246_mv', 'mv(PIDmv)', '{\"modleId\":43,\"resource\":\"modle\",\"modlepinsId\":246,\"outputpinmappingtagname\":\"A_SLM1_MV2(A_SLM1_MV2)\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 12:37:12', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (257, 41, 'A_SLM1_MV2', 'A_SLM1_MV2', 'A_SLM1_MV2(A_SLM1_MV2)', '{\"resource\":\"opc\",\"modlepinsId\":256,\"outmappingtag\":\"A_SLM1_MV2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 12:37:12', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (258, 50, '141_A_SLM1_AM1', 'auto', 'A_SLM1_AM1(A_SLM1_AM1(A_SLM1_AM1))\n                                            ', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":141}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 13:51:25', 'auto', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (259, 36, '152_A41E1AH04I', '152_A41E1AH04I', 'A41E1AH04I(A41E1AH04I(原料磨主电机电流))', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":152}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-29 17:51:36', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (260, 36, '36_152_A41E1AH04I', '36_152_A41E1AH04I', 'A41E1AH04I(A41E1AH04I(原料磨主电机电流))', '{\"modleId\":36,\"resource\":\"memory\",\"modlepinsId\":259}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-29 17:51:48', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (261, 50, '260_36_152_A41E1AH04I', 'pv1', '36_152_A41E1AH04I(A41E1AH04I(A41E1AH04I(原料磨主电机电流)))', '{\"modleId\":36,\"resource\":\"modle\",\"modlepinsId\":260}', 0.15, 0, 2, 2, 0, 0, 0.7, 'fullfunnel', 1, '2021-01-30 13:45:08', 'pv', 'after', 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (262, 50, '', 'pvup1', '', '{\"resource\":\"constant\",\"value\":200.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 13:45:08', 'pvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (263, 50, '', 'pvdown1', '', '{\"resource\":\"constant\",\"value\":120.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 13:45:08', 'pvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (264, 50, '149_A_SLM1_SV5', 'sp1', 'A_SLM1_SV5(A_SLM1_SV5(A_SLM1_SV5))', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":149}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 13:45:08', 'sp', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (265, 50, 'mv1', 'mv1', 'MPC2mv1', '{\"resource\":\"memory\"}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-29 18:01:21', NULL, NULL, 'output', 'mpcproperty');
INSERT INTO `modlepins` VALUES (266, 51, '7_A_BLJ_MV', 'BLJ_MV1', 'A_BLJ_MV(A_BLJ_MV)', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":7}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-29 18:27:27', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (267, 1, 'A_BLJ_MV2', 'A_BLJ_MV2', 'A_BLJ_MV2(2段篦速输出)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_BLJ_MV2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-29 18:26:07', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (268, 1, 'A_BLJ_2PC', 'A_BLJ_2PC', 'A_BLJ_2PC(12段偏差)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_BLJ_2PC\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-29 18:26:43', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (269, 1, 'A_BLJ_DB', 'A_BLJ_DB', 'A_BLJ_DB(篦冷机控制死区)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_BLJ_DB\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-29 18:26:56', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (270, 51, '267_A_BLJ_MV2', 'BLJ_MV2', 'A_BLJ_MV2(A_BLJ_MV2(2段篦速输出))', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":267}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-29 18:27:34', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (271, 51, '268_A_BLJ_2PC', 'BLJ_2PC', 'A_BLJ_2PC(A_BLJ_2PC(12段偏差))', '{\"modleId\":1,\"resource\":\"modle\",\"modlepinsId\":268}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-29 18:27:45', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (272, 51, 'pin_out', 'pin_out', '2段输出', '{\"resource\":\"memory\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-29 18:28:47', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (273, 40, 'A_SLM1_SV1', 'A_SLM1_SV1', 'A_SLM1_SV1(A_SLM1_SV1)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_SV1\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 09:25:59', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (274, 40, 'A_SLM1_HSV', 'A_SLM1_HSV', 'A_SLM1_HSV(流量自动设定)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_HSV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 09:29:18', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (276, 40, 'A_SLM1_LSV', 'A_SLM1_LSV', 'A_SLM1_LSV(流量手动设定)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_LSV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 09:35:40', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (277, 52, '245_AI41059F01', '245_AI41059F01', 'AI41059F01(AI41059F01(立磨喷水流量))', '{\"modleId\":40,\"resource\":\"modle\",\"modlepinsId\":245}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 09:59:52', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (278, 52, '52_245_AI41059F01', '52_245_AI41059F01', 'AI41059F01(AI41059F01(立磨喷水流量))', '{\"modleId\":52,\"resource\":\"memory\",\"modlepinsId\":277}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 09:59:59', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (279, 33, 'A_SLM1_HSV', 'A_SLM1_HSV', 'A_SLM1_HSV(流量自动设定)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_HSV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 12:47:31', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (280, 50, '279_A_SLM1_HSV', 'mv1', 'A_SLM1_HSV(A_SLM1_HSV(流量自动设定))', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":279}', 0, 0.2, 0, 0, 5, 0.05, 0, NULL, 1, '2021-01-30 12:49:13', 'mv', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (281, 50, '', 'mvup1', '', '{\"resource\":\"constant\",\"value\":10.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 12:49:13', 'mvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (282, 50, '', 'mvdown1', '', '{\"resource\":\"constant\",\"value\":0.5}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 12:49:13', 'mvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (283, 50, '279_A_SLM1_HSV', 'mvfb1', 'A_SLM1_HSV(A_SLM1_HSV(流量自动设定))', '{\"modleId\":33,\"resource\":\"modle\",\"modlepinsId\":279}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 12:49:13', 'mvfb', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (284, 34, '265_mv1', '265_mv1', 'mv1(MPC2mv1)', '{\"modleId\":50,\"resource\":\"modle\",\"modlepinsId\":265,\"outputpinmappingtagname\":\"A_SLM1_HSV(流量自动设定)\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 12:49:52', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (285, 34, 'A_SLM1_HSV', 'A_SLM1_HSV', 'A_SLM1_HSV(流量自动设定)', '{\"resource\":\"opc\",\"modlepinsId\":284,\"outmappingtag\":\"A_SLM1_HSV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 12:49:52', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (286, 8, '50.0', 'deadZone', '', '{\"resource\":\"constant\",\"value\":50.0}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:48:05', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (287, 4, '60.0', 'deadZone', '', '{\"resource\":\"constant\",\"value\":60.0}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:48:24', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (288, 43, '0.01', 'deadZone', '', '{\"resource\":\"constant\",\"value\":0.01}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 14:49:00', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (289, 35, 'dpv4mv1', 'dpv4mv1', 'dpv4mv1', '{\"resource\":\"memory\"}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 14:56:14', NULL, NULL, 'output', 'mpcproperty');
INSERT INTO `modlepins` VALUES (290, 35, 'dpv5mv1', 'dpv5mv1', 'dpv5mv1', '{\"resource\":\"memory\"}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 14:55:55', NULL, NULL, 'output', 'mpcproperty');
INSERT INTO `modlepins` VALUES (291, 53, 'A_SLM1_AM1', 'A_SLM1_AM1', 'A_SLM1_AM1(A_SLM1_AM1)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_AM1\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:53:56', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (292, 53, 'A_SLM1_AM2', 'A_SLM1_AM2', 'A_SLM1_AM2(A_SLM1_AM2)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_AM2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:54:08', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (293, 53, 'A_SLM1_HI1', 'A_SLM1_HI1', 'A_SLM1_HI1(A_SLM1_HI1)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_HI1\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:54:13', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (294, 53, 'A_SLM1_LO1', 'A_SLM1_LO1', 'A_SLM1_LO1(A_SLM1_LO1)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_LO1\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:54:17', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (295, 53, 'A_SLM1_MV1', 'A_SLM1_MV1', 'A_SLM1_MV1(A_SLM1_MV1)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_MV1\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:54:28', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (296, 53, 'A_SLM1_SV1', 'A_SLM1_SV1', 'A_SLM1_SV1(A_SLM1_SV1)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_SV1\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:54:43', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (297, 53, 'A_SLM1_SV2', 'A_SLM1_SV2', 'A_SLM1_SV2(A_SLM1_SV2)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_SV2\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:54:48', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (298, 53, 'A_SLM1_SV3', 'A_SLM1_SV3', 'A_SLM1_SV3(A_SLM1_SV3)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_SV3\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:54:55', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (299, 53, 'A_SLM1_SV4', 'A_SLM1_SV4', 'A_SLM1_SV4(A_SLM1_SV4)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_SV4\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:55:01', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (300, 53, 'A_SLM1_SV5', 'A_SLM1_SV5', 'A_SLM1_SV5(A_SLM1_SV5)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_SV5\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:55:06', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (301, 53, 'A_SLM1_HSV', 'A_SLM1_HSV', 'A_SLM1_HSV(流量自动设定)', '{\"resource\":\"opc\",\"inmappingtag\":\"A_SLM1_HSV\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:55:13', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (302, 53, 'AI4105T02', 'AI4105T02', 'AI4105T02(出磨风温)', '{\"resource\":\"opc\",\"inmappingtag\":\"AI4105T02\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:55:49', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (303, 53, 'AI41054L', 'AI41054L', 'AI41054L(料层厚度 (pv))', '{\"resource\":\"opc\",\"inmappingtag\":\"AI41054L\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:56:02', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (304, 53, 'A41E1AH04I', 'A41E1AH04I', 'A41E1AH04I(原料磨主电机电流)', '{\"resource\":\"opc\",\"inmappingtag\":\"A41E1AH04I\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:56:21', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (305, 53, 'A41E1AH05I', 'A41E1AH05I', 'A41E1AH05I(循环风机主电机电流)', '{\"resource\":\"opc\",\"inmappingtag\":\"A41E1AH05I\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:56:29', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (306, 55, '303_AI41054L', '303_AI41054L', 'AI41054L(AI41054L(料层厚度 (pv)))', '{\"modleId\":53,\"resource\":\"modle\",\"modlepinsId\":303}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:58:07', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (307, 55, '55_303_AI41054L', '55_303_AI41054L', 'AI41054L(AI41054L(料层厚度 (pv)))', '{\"modleId\":55,\"resource\":\"memory\",\"modlepinsId\":306}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:58:15', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (308, 55, '304_A41E1AH04I', '304_A41E1AH04I', 'A41E1AH04I(A41E1AH04I(原料磨主电机电流))', '{\"modleId\":53,\"resource\":\"modle\",\"modlepinsId\":304}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:58:27', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (309, 55, '55_304_A41E1AH04I', '55_304_A41E1AH04I', 'A41E1AH04I(A41E1AH04I(原料磨主电机电流))', '{\"modleId\":55,\"resource\":\"memory\",\"modlepinsId\":308}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:58:39', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (310, 53, 'AI4108I', 'AI4108I', 'AI4108I(提升机电流)', '{\"resource\":\"opc\",\"inmappingtag\":\"AI4108I\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:59:23', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (311, 55, '310_AI4108I', '310_AI4108I', 'AI4108I(AI4108I(提升机电流))', '{\"modleId\":53,\"resource\":\"modle\",\"modlepinsId\":310}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:59:33', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (312, 55, '55_310_AI4108I', '55_310_AI4108I', 'AI4108I(AI4108I(提升机电流))', '{\"modleId\":55,\"resource\":\"memory\",\"modlepinsId\":311}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 15:59:39', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (313, 56, '1.0', 'auto', '', '{\"resource\":\"constant\",\"value\":1.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:57:30', 'auto', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (314, 56, '312_55_310_AI4108I', 'pv1', '55_310_AI4108I(AI4108I(AI4108I(提升机电流)))', '{\"modleId\":55,\"resource\":\"modle\",\"modlepinsId\":312}', 1.2, 0, 2, 1, 0, 0, 0.6, 'fullfunnel', 1, '2021-01-30 16:18:40', 'pv', 'after', 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (315, 56, '', 'pvup1', '', '{\"resource\":\"constant\",\"value\":40.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:18:40', 'pvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (316, 56, '', 'pvdown1', '', '{\"resource\":\"constant\",\"value\":20.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:18:40', 'pvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (317, 56, '297_A_SLM1_SV2', 'sp1', 'A_SLM1_SV2(A_SLM1_SV2(A_SLM1_SV2))', '{\"modleId\":53,\"resource\":\"modle\",\"modlepinsId\":297}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:18:40', 'sp', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (318, 56, '307_55_303_AI41054L', 'pv2', '55_303_AI41054L(AI41054L(AI41054L(料层厚度 (pv))))', '{\"modleId\":55,\"resource\":\"modle\",\"modlepinsId\":307}', 0.2, 0, 2, 2, 0, 0, 0.6, 'fullfunnel', 1, '2021-01-30 16:18:57', 'pv', 'after', 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (319, 56, '', 'pvup2', '', '{\"resource\":\"constant\",\"value\":150.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:18:57', 'pvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (320, 56, '', 'pvdown2', '', '{\"resource\":\"constant\",\"value\":30.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:18:57', 'pvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (321, 56, '299_A_SLM1_SV4', 'sp2', 'A_SLM1_SV4(A_SLM1_SV4(A_SLM1_SV4))', '{\"modleId\":53,\"resource\":\"modle\",\"modlepinsId\":299}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:18:57', 'sp', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (326, 56, '295_A_SLM1_MV1', 'mv1', 'A_SLM1_MV1(A_SLM1_MV1(A_SLM1_MV1))', '{\"modleId\":53,\"resource\":\"modle\",\"modlepinsId\":295}', 0, 3, 0, 0, 40, 0.05, 0, NULL, 1, '2021-01-30 16:04:56', 'mv', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (327, 56, '293_A_SLM1_HI1', 'mvup1', 'A_SLM1_HI1(A_SLM1_HI1(A_SLM1_HI1))', '{\"modleId\":53,\"resource\":\"modle\",\"modlepinsId\":293}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:04:56', 'mvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (328, 56, '294_A_SLM1_LO1', 'mvdown1', 'A_SLM1_LO1(A_SLM1_LO1(A_SLM1_LO1))', '{\"modleId\":53,\"resource\":\"modle\",\"modlepinsId\":294}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:04:56', 'mvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (329, 56, '295_A_SLM1_MV1', 'mvfb1', 'A_SLM1_MV1(A_SLM1_MV1(A_SLM1_MV1))', '{\"modleId\":53,\"resource\":\"modle\",\"modlepinsId\":295}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:04:56', 'mvfb', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (330, 55, '305_A41E1AH05I', '305_A41E1AH05I', 'A41E1AH05I(A41E1AH05I(循环风机主电机电流))', '{\"modleId\":53,\"resource\":\"modle\",\"modlepinsId\":305}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 16:05:53', NULL, NULL, 'input', 'basepropery');
INSERT INTO `modlepins` VALUES (331, 55, '55_305_A41E1AH05I', '55_305_A41E1AH05I', 'A41E1AH05I(A41E1AH05I(循环风机主电机电流))', '{\"modleId\":55,\"resource\":\"memory\",\"modlepinsId\":330}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 16:05:59', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (332, 53, 'AI4105101S', 'AI4105101S', 'AI4105101S(选粉机变频速度反馈)', '{\"resource\":\"opc\",\"inmappingtag\":\"AI4105101S\"}', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, '2021-01-30 16:06:24', NULL, NULL, 'output', 'basepropery');
INSERT INTO `modlepins` VALUES (333, 56, '331_55_305_A41E1AH05I', 'ff1', '55_305_A41E1AH05I(A41E1AH05I(A41E1AH05I(循环风机主电机电流)))', '{\"modleId\":55,\"resource\":\"modle\",\"modlepinsId\":331}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:07:11', 'ff', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (334, 56, '', 'ffup1', '', '{\"resource\":\"constant\",\"value\":200.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:07:11', 'ffup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (335, 56, '', 'ffdown1', '', '{\"resource\":\"constant\",\"value\":160.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:07:11', 'ffdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (336, 56, '332_AI4105101S', 'ff2', 'AI4105101S(AI4105101S(选粉机变频速度反馈))', '{\"modleId\":53,\"resource\":\"modle\",\"modlepinsId\":332}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:07:36', 'ff', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (337, 56, '', 'ffup2', '', '{\"resource\":\"constant\",\"value\":1300.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:07:36', 'ffup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (338, 56, '', 'ffdown2', '', '{\"resource\":\"constant\",\"value\":1000.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:07:36', 'ffdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (339, 56, 'mv1', 'mv1', 'MPCmv1', '{\"resource\":\"memory\"}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:13:05', NULL, NULL, 'output', 'mpcproperty');
INSERT INTO `modlepins` VALUES (344, 56, '304_A41E1AH04I', 'pv3', 'A41E1AH04I(A41E1AH04I(原料磨主电机电流))', '{\"modleId\":53,\"resource\":\"modle\",\"modlepinsId\":304}', 0.18, 0, 2, 1, 0, 0, 0.7, 'fullfunnel', 1, '2021-01-30 16:56:38', 'pv', 'before', 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (345, 56, '', 'pvup3', '', '{\"resource\":\"constant\",\"value\":200.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:56:38', 'pvup', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (346, 56, '', 'pvdown3', '', '{\"resource\":\"constant\",\"value\":100.0}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:56:38', 'pvdown', NULL, 'input', 'mpcproperty');
INSERT INTO `modlepins` VALUES (347, 56, '300_A_SLM1_SV5', 'sp3', 'A_SLM1_SV5(A_SLM1_SV5(A_SLM1_SV5))', '{\"modleId\":53,\"resource\":\"modle\",\"modlepinsId\":300}', 0, 0, 0, 0, 0, 0, 0, NULL, 1, '2021-01-30 16:56:38', 'sp', NULL, 'input', 'mpcproperty');

-- ----------------------------
-- Table structure for modlerespon
-- ----------------------------
DROP TABLE IF EXISTS `modlerespon`;
CREATE TABLE `modlerespon`  (
  `modleresponId` int(11) NOT NULL AUTO_INCREMENT,
  `refrencemodleId` int(11) NULL DEFAULT NULL COMMENT '模型id',
  `stepRespJson` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `inputPins` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '引脚名称：mvn ffn等n=1,2,3..8',
  `outputPins` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '引脚名称：pvn 等n=1,2,3..8',
  `effectRatio` float NULL DEFAULT 1 COMMENT '作用比例\r\npv1 ef1\r\npv2 ef2\r\npv1 和pv2都收到mv1的影响，\r\npv1对mv1计算出的dmv1\r\npv2对mv1计算出的dmv2，\r\n作用于mv1的pv1部分为dmv1*ef1/(ef1+ef2)\r\n',
  PRIMARY KEY (`modleresponId`) USING BTREE,
  INDEX `refrencemodleId`(`refrencemodleId`) USING BTREE,
  CONSTRAINT `modlerespon_ibfk_1` FOREIGN KEY (`refrencemodleId`) REFERENCES `modle` (`modleId`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modlerespon
-- ----------------------------
INSERT INTO `modlerespon` VALUES (1, 9, '{\"tao\":60.0,\"t\":700.0,\"k\":-2600.0}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (2, 13, '{\"tao\":35.0,\"t\":185.0,\"k\":27.0}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (3, 17, '{\"tao\":20.0,\"t\":160.0,\"k\":-6.5}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (4, 26, '{\"tao\":30.0,\"t\":170.0,\"k\":-2.0}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (5, 26, '{\"tao\":37.0,\"t\":320.0,\"k\":-0.32}', 'mv1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (6, 13, '{\"tao\":35.0,\"t\":150.0,\"k\":0.06}', 'ff1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (7, 30, '{\"tao\":30.0,\"t\":170.0,\"k\":4.5}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (8, 30, '{\"tao\":35.0,\"t\":220.0,\"k\":0.32}', 'mv1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (10, 35, '{\"tao\":60.0,\"t\":240.0,\"k\":0.5}', 'mv1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (11, 35, '{\"tao\":60.0,\"t\":280.0,\"k\":5.0}', 'mv1', 'pv4', 1);
INSERT INTO `modlerespon` VALUES (12, 35, '{\"tao\":60.0,\"t\":260.0,\"k\":7.0}', 'mv1', 'pv5', 1);
INSERT INTO `modlerespon` VALUES (13, 35, '{\"tao\":30.0,\"t\":180.0,\"k\":-5.0}', 'ff1', 'pv4', 1);
INSERT INTO `modlerespon` VALUES (14, 35, '{\"tao\":30.0,\"t\":190.0,\"k\":-2.0}', 'ff1', 'pv5', 1);
INSERT INTO `modlerespon` VALUES (15, 35, '{\"tao\":30.0,\"t\":240.0,\"k\":3.0}', 'ff2', 'pv4', 1);
INSERT INTO `modlerespon` VALUES (16, 35, '{\"tao\":30.0,\"t\":240.0,\"k\":2.0}', 'ff2', 'pv5', 1);
INSERT INTO `modlerespon` VALUES (17, 46, '{\"tao\":60.0,\"t\":380.0,\"k\":-3.8}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (18, 46, '{\"tao\":60.0,\"t\":320.0,\"k\":-4.5}', 'mv1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (19, 50, '{\"tao\":15.0,\"t\":120.0,\"k\":20.0}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (20, 56, '{\"tao\":60.0,\"t\":240.0,\"k\":0.5}', 'mv1', 'pv1', 1);
INSERT INTO `modlerespon` VALUES (21, 56, '{\"tao\":60.0,\"t\":280.0,\"k\":5.0}', 'mv1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (23, 56, '{\"tao\":30.0,\"t\":180.0,\"k\":-5.0}', 'ff1', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (25, 56, '{\"tao\":30.0,\"t\":240.0,\"k\":3.0}', 'ff2', 'pv2', 1);
INSERT INTO `modlerespon` VALUES (28, 56, '{\"tao\":60.0,\"t\":260.0,\"k\":7.0}', 'mv1', 'pv3', 1);

-- ----------------------------
-- Table structure for modlesight
-- ----------------------------
DROP TABLE IF EXISTS `modlesight`;
CREATE TABLE `modlesight`  (
  `modlesightid` int(11) NOT NULL AUTO_INCREMENT,
  `positiontop` double NULL DEFAULT NULL,
  `positionleft` double NULL DEFAULT NULL,
  `childs` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子节点信息',
  `refmodleid` int(11) NULL DEFAULT NULL,
  `parents` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父节点信息',
  PRIMARY KEY (`modlesightid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of modlesight
-- ----------------------------
INSERT INTO `modlesight` VALUES (1, 50, 224, '[{\"id\":3},{\"id\":4},{\"id\":8},{\"id\":9},{\"id\":51}]', 1, '[]');
INSERT INTO `modlesight` VALUES (2, 702, 456, '[]', 2, '[{\"id\":4},{\"id\":8},{\"id\":9},{\"id\":51}]');
INSERT INTO `modlesight` VALUES (3, 187, 535, '[{\"id\":4},{\"id\":8},{\"id\":9}]', 3, '[{\"id\":1}]');
INSERT INTO `modlesight` VALUES (4, 425, 392, '[{\"id\":2}]', 4, '[{\"id\":3},{\"id\":1}]');
INSERT INTO `modlesight` VALUES (8, 423, 148, '[{\"id\":2}]', 8, '[{\"id\":1},{\"id\":3}]');
INSERT INTO `modlesight` VALUES (9, 426, 648, '[{\"id\":2}]', 9, '[{\"id\":1},{\"id\":3}]');
INSERT INTO `modlesight` VALUES (11, 15, 294, '[{\"id\":14},{\"id\":13}]', 11, '[]');
INSERT INTO `modlesight` VALUES (12, 630, 574, '[]', 12, '[{\"id\":13}]');
INSERT INTO `modlesight` VALUES (13, 354, 289, '[{\"id\":12}]', 13, '[{\"id\":11},{\"id\":14}]');
INSERT INTO `modlesight` VALUES (14, 211, 569, '[{\"id\":13}]', 14, '[{\"id\":11}]');
INSERT INTO `modlesight` VALUES (15, 49, 125, '[{\"id\":18},{\"id\":17}]', 15, '[]');
INSERT INTO `modlesight` VALUES (16, 662, 319, '[]', 16, '[{\"id\":17},{\"id\":18}]');
INSERT INTO `modlesight` VALUES (17, 352, 318, '[{\"id\":16}]', 17, '[{\"id\":15},{\"id\":18}]');
INSERT INTO `modlesight` VALUES (18, 208, 546, '[{\"id\":17},{\"id\":16}]', 18, '[{\"id\":15}]');
INSERT INTO `modlesight` VALUES (19, 32, 237, '[{\"id\":37}]', 19, '[]');
INSERT INTO `modlesight` VALUES (22, 304, 251, '[]', 22, '[{\"id\":37}]');
INSERT INTO `modlesight` VALUES (24, 89, 399, '[{\"id\":26},{\"id\":30}]', 24, '[]');
INSERT INTO `modlesight` VALUES (25, 533, 403, '[]', 25, '[{\"id\":26},{\"id\":30}]');
INSERT INTO `modlesight` VALUES (26, 302, 300, '[{\"id\":25}]', 26, '[{\"id\":24}]');
INSERT INTO `modlesight` VALUES (30, 308, 504, '[{\"id\":25}]', 30, '[{\"id\":24}]');
INSERT INTO `modlesight` VALUES (33, 51, 105, '[{\"id\":35},{\"id\":36},{\"id\":50}]', 33, '[]');
INSERT INTO `modlesight` VALUES (34, 669, 743, '[]', 34, '[{\"id\":35},{\"id\":50}]');
INSERT INTO `modlesight` VALUES (35, 375, 279, '[{\"id\":34}]', 35, '[{\"id\":33},{\"id\":36}]');
INSERT INTO `modlesight` VALUES (36, 195, 705, '[{\"id\":35},{\"id\":50}]', 36, '[{\"id\":33}]');
INSERT INTO `modlesight` VALUES (37, 168, 417, '[{\"id\":22}]', 37, '[{\"id\":19}]');
INSERT INTO `modlesight` VALUES (38, 86, 264, '[{\"id\":47}]', 38, '[]');
INSERT INTO `modlesight` VALUES (39, 476, 264, '[]', 39, '[{\"id\":47}]');
INSERT INTO `modlesight` VALUES (40, 126, 569, '[{\"id\":43},{\"id\":52}]', 40, '[]');
INSERT INTO `modlesight` VALUES (41, 663, 579, '[]', 41, '[{\"id\":43}]');
INSERT INTO `modlesight` VALUES (43, 270, 156, '[{\"id\":41}]', 43, '[{\"id\":40},{\"id\":52}]');
INSERT INTO `modlesight` VALUES (44, 83, 195, '[{\"id\":46}]', 44, '[]');
INSERT INTO `modlesight` VALUES (45, 510, 195, '[]', 45, '[{\"id\":46}]');
INSERT INTO `modlesight` VALUES (46, 297, 195, '[{\"id\":45}]', 46, '[{\"id\":44}]');
INSERT INTO `modlesight` VALUES (47, 274, 264, '[{\"id\":39}]', 47, '[{\"id\":38}]');
INSERT INTO `modlesight` VALUES (50, 381, 524, '[{\"id\":34}]', 50, '[{\"id\":36},{\"id\":33}]');
INSERT INTO `modlesight` VALUES (51, 567, 305, '[{\"id\":2}]', 51, '[{\"id\":1}]');
INSERT INTO `modlesight` VALUES (52, 269, 563, '[{\"id\":43}]', 52, '[{\"id\":40}]');
INSERT INTO `modlesight` VALUES (53, 29, 372, '[{\"id\":55},{\"id\":56}]', 53, '[]');
INSERT INTO `modlesight` VALUES (54, 556, 220, '[]', 54, '[{\"id\":56}]');
INSERT INTO `modlesight` VALUES (55, 185, 623, '[{\"id\":56}]', 55, '[{\"id\":53}]');
INSERT INTO `modlesight` VALUES (56, 241, 198, '[{\"id\":54}]', 56, '[{\"id\":55},{\"id\":53}]');
INSERT INTO `modlesight` VALUES (57, 28.666667938232422, 252, '[{\"id\":58}]', 57, '[]');
INSERT INTO `modlesight` VALUES (58, 206.6666717529297, 301, '[{\"id\":59}]', 58, '[{\"id\":57}]');
INSERT INTO `modlesight` VALUES (59, 373.6666717529297, 339, '[]', 59, '[{\"id\":58}]');

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project`  (
  `projectid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工程名称',
  `runperiod` double NULL DEFAULT NULL COMMENT '运行周期\r\n',
  PRIMARY KEY (`projectid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '工程' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of project
-- ----------------------------
INSERT INTO `project` VALUES (1, '篦冷机层压控制', 3);
INSERT INTO `project` VALUES (2, '分解炉喂煤控制', 10);
INSERT INTO `project` VALUES (3, '窑头排转速控制', 10);
INSERT INTO `project` VALUES (4, '煤磨温度控制', 12);
INSERT INTO `project` VALUES (5, '煤磨产量控制', 15);
INSERT INTO `project` VALUES (6, '生料磨喷水控制', 4);
INSERT INTO `project` VALUES (7, '生料磨产量控制', 15);
INSERT INTO `project` VALUES (8, '余热锅炉水位控制', 15);
INSERT INTO `project` VALUES (9, '平台通讯校验', 3);
INSERT INTO `project` VALUES (10, '生料磨产量控制2', 15);
INSERT INTO `project` VALUES (11, 'test', 15);

SET FOREIGN_KEY_CHECKS = 1;
