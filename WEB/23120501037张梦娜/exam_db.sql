/*
 Navicat Premium Dump SQL

 Source Server         : exam123
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : exam_db

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 05/01/2026 11:17:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for exam_subject
-- ----------------------------
DROP TABLE IF EXISTS `exam_subject`;
CREATE TABLE `exam_subject`  (
  `subject_id` int NOT NULL AUTO_INCREMENT COMMENT '科目编号',
  `subject_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '考试科目名称',
  `exam_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '考试类型',
  PRIMARY KEY (`subject_id`) USING BTREE,
  UNIQUE INDEX `subject_name`(`subject_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试科目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_subject
-- ----------------------------
INSERT INTO `exam_subject` VALUES (3, '科目1', '类型1');
INSERT INTO `exam_subject` VALUES (4, '科目2', '类型2');
INSERT INTO `exam_subject` VALUES (5, '科目3', '类型3');
INSERT INTO `exam_subject` VALUES (6, '科目4', '类型4');
INSERT INTO `exam_subject` VALUES (7, '科目5', '类型5');

-- ----------------------------
-- Table structure for proctor_info
-- ----------------------------
DROP TABLE IF EXISTS `proctor_info`;
CREATE TABLE `proctor_info`  (
  `proctor_id` int NOT NULL AUTO_INCREMENT COMMENT '监考ID（主键自增）',
  `subject_id` int NOT NULL COMMENT '关联exam_subject表的科目ID',
  `subject_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '科目名称（冗余存储）',
  `teacher_id` int NOT NULL COMMENT '关联teacher表的教师ID',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师姓名（冗余存储）',
  `exam_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '考试时间',
  `exam_place` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '考试地点',
  PRIMARY KEY (`proctor_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '监考信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of proctor_info
-- ----------------------------
INSERT INTO `proctor_info` VALUES (3, 3, '科目1', 1, 'tea1', '1', '11');
INSERT INTO `proctor_info` VALUES (4, 4, '科目2', 2, 'tea2', '23', '2323');
INSERT INTO `proctor_info` VALUES (5, 5, '科目3', 1, 'tea1', '14', '1414');
INSERT INTO `proctor_info` VALUES (6, 6, '科目4', 1, 'tea1', '13', '1313');
INSERT INTO `proctor_info` VALUES (7, 7, '科目5', 5, 'tea5', '5', '55');

-- ----------------------------
-- Table structure for proctor_teacher_relation
-- ----------------------------
DROP TABLE IF EXISTS `proctor_teacher_relation`;
CREATE TABLE `proctor_teacher_relation`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '关联表主键ID',
  `proctor_id` int NOT NULL COMMENT '监考信息ID（关联proctor_info表的proctor_id）',
  `teacher_id` int NOT NULL COMMENT '教师ID（关联teacher表的teacher_id）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_proctor_teacher`(`proctor_id` ASC, `teacher_id` ASC) USING BTREE,
  INDEX `teacher_id`(`teacher_id` ASC) USING BTREE,
  CONSTRAINT `proctor_teacher_relation_ibfk_1` FOREIGN KEY (`proctor_id`) REFERENCES `proctor_info` (`proctor_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `proctor_teacher_relation_ibfk_2` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`teacher_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '监考-教师多对多关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of proctor_teacher_relation
-- ----------------------------
INSERT INTO `proctor_teacher_relation` VALUES (4, 3, 1);
INSERT INTO `proctor_teacher_relation` VALUES (5, 4, 2);
INSERT INTO `proctor_teacher_relation` VALUES (6, 4, 3);
INSERT INTO `proctor_teacher_relation` VALUES (14, 5, 1);
INSERT INTO `proctor_teacher_relation` VALUES (15, 5, 4);
INSERT INTO `proctor_teacher_relation` VALUES (16, 6, 1);
INSERT INTO `proctor_teacher_relation` VALUES (17, 6, 3);
INSERT INTO `proctor_teacher_relation` VALUES (13, 7, 5);

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher`  (
  `teacher_id` int NOT NULL AUTO_INCREMENT COMMENT '教师ID（主键自增）',
  `teacher_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师姓名',
  `teacher_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师工号（唯一，不重复）',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '联系电话',
  `college` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '所属学院',
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教师登录密码（由管理员设置）',
  PRIMARY KEY (`teacher_id`) USING BTREE,
  UNIQUE INDEX `teacher_no`(`teacher_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教师信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of teacher
-- ----------------------------
INSERT INTO `teacher` VALUES (1, 'tea1', 'tea1', '1', '11', 'tea1');
INSERT INTO `teacher` VALUES (2, 'tea2', 'tea2', '2', '22', 'tea2');
INSERT INTO `teacher` VALUES (3, 'tea3', 'tea3', '3', '33', 'tea3');
INSERT INTO `teacher` VALUES (4, 'tea4', 'tea4', '4', '44', 'tea4');
INSERT INTO `teacher` VALUES (5, 'tea5', 'tea5', '5', '55', 'tea5');

SET FOREIGN_KEY_CHECKS = 1;
