/*
 Navicat Premium Dump SQL

 Source Server         : 1
 Source Server Type    : MySQL
 Source Server Version : 50733 (5.7.33)
 Source Host           : localhost:3306
 Source Schema         : yunyou

 Target Server Type    : MySQL
 Target Server Version : 50733 (5.7.33)
 File Encoding         : 65001

 Date: 20/12/2024 01:07:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userAccount` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `Stars` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `friendAccount` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 93 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feedback
-- ----------------------------
INSERT INTO `feedback` VALUES (86, '123', '北京工业大学北京工业大学学生宿舍3号楼', 'camera<img src=\"content://com.yunyou.fileprovider/my_images/Android/data/com.zzdayss.yunyou/cache/28c02d82-1398-48b0-a032-925308d819fc.jpg\" alt=\"image\" width=\"300\" height=\"200\"><br>album<br><img src=\"content://com.yunyou.fileprovider/my_images/Android/data/com.zzdayss.yunyou/cache/bc88c6e5-48c6-4fb9-9983-6a671c95ff62.jpg\" alt=\"image\" width=\"300\" height=\"200\"><br>', '4.5', NULL);
INSERT INTO `feedback` VALUES (87, '123', '北京市海淀区青龙桥街道颐和园颐和园-万寿山', 'DAY1°雍和宫<br><br>下午一点左右到达北京后，立马就去了雍和宫，里面手串种类还是蛮多的，价格有点贵，而且排队的人很多。°天坛体力原因，选择了1h路线，即东门进一长廊一祈年殿一丹陛桥一回音壁一圜丘一南门出。<br><br>DAY2°北京野生动物园<br><br>位置非常远，地铁+打车花了两个半小时，但看到小熊猫后这一切又是值得的，真的太太太可爱了，亲人一点的还可以摸摸，巨萌！喜欢动物喜欢自然的，我觉得还是值得去一趟的，各类动物非常多，互动体验也不少。萌宠乐园可去可不去，小老虎幼崽可以由工作人员抱着合影但不能摸摸。<br><br>DAY3°顾和园<br><br>懒人路线：北如意门进一画中游（猛猛拍照）一长廊一佛香阁一十七孔桥一东宫门出建议去之前确定好想看的景点，然后再根据实际情况增加删减，联票不建议买，因为根本逛不完，先买门票就好。°天安门广场一降旗早起是不可能的，所以逛完天安门城楼后，去看了降旗仪式。<br><img src=\"content://com.yunyou.fileprovider/my_images/Android/data/com.zzdayss.yunyou/cache/b83f6844-8292-483d-96ca-2954a051f048.jpg\" alt=\"image\" width=\"300\" height=\"200\"><br>', '4.0', NULL);
INSERT INTO `feedback` VALUES (89, '123', '北京市朝阳区南磨房乡北京工业大学北京工业大学校医院', 'null游览北京！Very exciting to visit Beijing.Here are some thoughtswe had:An incredible amount of people,especially tourists.You are never alone,but we still felt incredibly safe.The city is huge,it\'s easy to underestimate thetime it takes to travel between sights and even walkwithin them!Not many people speak English,but the people arestill helpful and welcoming!Modern city but still well-preserved culturalheritage.The subway works smoothly (although wecouldn\'t pay with our faces )Even though thereare 400 stations,more would be needed consideringthe size of the city though!<img src=\"content://com.yunyou.fileprovider/my_images/Android/data/com.zzdayss.yunyou/cache/8439d7ce-16ea-4ea4-8654-d8119a5d967f.jpg\" alt=\"image\" width=\"300\" height=\"200\"><br><img src=\"content://com.yunyou.fileprovider/my_images/Android/data/com.zzdayss.yunyou/cache/e42ec611-4752-480b-8300-1d8d5c467b37.jpg\" alt=\"image\" width=\"300\" height=\"200\"><br>', '4.0', NULL);
INSERT INTO `feedback` VALUES (91, '123', '111', 'aaaa，kjj11111<br>，111qweqwe<br><span style=\"color: rgb(236, 200, 18);\"><font size=\"5\">11111<br>234242</font></span><br>453453<br><br>', '3.5', '1');

-- ----------------------------
-- Table structure for friends
-- ----------------------------
DROP TABLE IF EXISTS `friends`;
CREATE TABLE `friends`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userAccount` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `friendAccount` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of friends
-- ----------------------------
INSERT INTO `friends` VALUES (1, 'a', 'b', 1);
INSERT INTO `friends` VALUES (2, 'a', '1', 1);
INSERT INTO `friends` VALUES (3, 'a', '1', 1);
INSERT INTO `friends` VALUES (9, '123', '1', 1);

-- ----------------------------
-- Table structure for trip
-- ----------------------------
DROP TABLE IF EXISTS `trip`;
CREATE TABLE `trip`  (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `userAccount` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `departure` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `destination` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `date` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of trip
-- ----------------------------
INSERT INTO `trip` VALUES (1, '1', '广东', '北京', '2022年5月25日12时30分');
INSERT INTO `trip` VALUES (2, '1', '北京', '天津', '2022年5月19日20时22分43秒');
INSERT INTO `trip` VALUES (10, '1', '广东', '四川', '2022年4月23日1时55分57秒');
INSERT INTO `trip` VALUES (11, '1', '广东', '上海', '2022年5月31日3时55分');
INSERT INTO `trip` VALUES (12, '12345', '北京', '上海', '2024年10月30日20时5分28秒');
INSERT INTO `trip` VALUES (34, '123', '333', '444', '2024年10月27日16时18分26秒');

-- ----------------------------
-- Table structure for tripdetail
-- ----------------------------
DROP TABLE IF EXISTS `tripdetail`;
CREATE TABLE `tripdetail`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userAccount` int(255) NULL DEFAULT NULL,
  `date` int(11) NULL DEFAULT NULL,
  `position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 54 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tripdetail
-- ----------------------------
INSERT INTO `tripdetail` VALUES (24, 29, 312, '56');
INSERT INTO `tripdetail` VALUES (25, 29, 34, '56');
INSERT INTO `tripdetail` VALUES (26, 29, 1234, '1234');
INSERT INTO `tripdetail` VALUES (27, 29, 111, '111');
INSERT INTO `tripdetail` VALUES (41, 30, 20241003, 'position 1 -- position 2');
INSERT INTO `tripdetail` VALUES (50, 31, 20241101, 'position 1 -- position 2');
INSERT INTO `tripdetail` VALUES (51, 31, 20241102, 'position 3');
INSERT INTO `tripdetail` VALUES (53, 33, 1111, '1111');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `userAccount` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `userPassword` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `userName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `state` int(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`userAccount`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '1', 'admin', 0);
INSERT INTO `user` VALUES ('12', '12', 'guset', 0);
INSERT INTO `user` VALUES ('123', '123', 'a', 1);
INSERT INTO `user` VALUES ('1234', '123', 'ab', 1);
INSERT INTO `user` VALUES ('12345', '123', 'abc', 0);
INSERT INTO `user` VALUES ('123456', '123', 'abcd', 0);
INSERT INTO `user` VALUES ('1234567', '123', 'abcde', 0);
INSERT INTO `user` VALUES ('12346789', '123', 'abcdef', 0);
INSERT INTO `user` VALUES ('456', '456', '456', 0);
INSERT INTO `user` VALUES ('789', '789', '789', 0);

SET FOREIGN_KEY_CHECKS = 1;
