-- 图书分享系统业务表初始化脚本
-- 来源：本地 MySQL ry 库导出（dump-ry-202609161352.sql）提取 9 张业务表
-- 导入顺序：先导入 ry_20250522.sql（若依系统表+管理员数据），再导入本文件

-- ==============================================
-- Table: book_comment
-- ==============================================
--
-- Table structure for table `book_comment`
--

DROP TABLE IF EXISTS `book_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_comment` (
  `comment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `book_id` bigint NOT NULL COMMENT '书籍ID',
  `user_id` bigint NOT NULL COMMENT '评论者ID',
  `user_name` varchar(64) DEFAULT NULL COMMENT '评论者昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '评论者头像',
  `content` text NOT NULL COMMENT '评论内容',
  `root_id` bigint DEFAULT '0' COMMENT '根评论ID(一级评论为0)',
  `parent_id` bigint DEFAULT '0' COMMENT '父评论ID(直属上级)',
  `reply_to_id` bigint DEFAULT '0' COMMENT '被回复人ID',
  `reply_to_name` varchar(64) DEFAULT NULL COMMENT '被回复人昵称',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`comment_id`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_root_id` (`root_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图书多级评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ==============================================
-- Table: book_type
-- ==============================================
--
-- Table structure for table `book_type`
--

DROP TABLE IF EXISTS `book_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_type` (
  `book_type_id` bigint NOT NULL AUTO_INCREMENT COMMENT '图书标签ID',
  `book_type_name` varchar(255) NOT NULL COMMENT '图书标签名称',
  `book_type_sort` int NOT NULL COMMENT '显示顺序',
  `book_type_status` char(1) NOT NULL DEFAULT '0' COMMENT '标签状态（0正常 1停用）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`book_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图书标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ==============================================
-- Table: books
-- ==============================================
--
-- Table structure for table `books`
--

DROP TABLE IF EXISTS `books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `books` (
  `books_id` bigint NOT NULL AUTO_INCREMENT COMMENT '图书ID',
  `books_name` varchar(255) NOT NULL COMMENT '图书名称',
  `books_author` varchar(255) NOT NULL COMMENT '图书作者',
  `books_publisher` varchar(255) NOT NULL COMMENT '出版社',
  `books_type_id` bigint NOT NULL COMMENT '图书标签ID',
  `books_cover` varchar(500) DEFAULT '' COMMENT '图书封面',
  `books_description` text COMMENT '图书描述',
  `books_status` char(1) NOT NULL DEFAULT '0' COMMENT '图书状态（0上架 1下架）',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `total_stock` int NOT NULL DEFAULT '1' COMMENT '总库存数量',
  `available_stock` int NOT NULL DEFAULT '1' COMMENT '当前可借库存数量',
  `book_points_value` int NOT NULL COMMENT '图书积分价值(例如标价50元的书设定为500积分)',
  PRIMARY KEY (`books_id`),
  KEY `idx_books_type` (`books_type_id`),
  KEY `idx_books_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图书商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ==============================================
-- Table: points_record
-- ==============================================
--
-- Table structure for table `points_record`
--

DROP TABLE IF EXISTS `points_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `points_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '流水主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `trade_type` tinyint NOT NULL COMMENT '交易类型：1-充值，2-支付租金，3-冻结押金，4-解冻押金，5-获得共享收益，6-违约扣除押金',
  `amount` int NOT NULL COMMENT '变动数额（正数为增加，负数为减少）',
  `order_id` bigint DEFAULT NULL COMMENT '关联的业务ID(借阅订单ID或充值单号)',
  `remark` varchar(255) DEFAULT '' COMMENT '备注说明(例如：借阅《Java编程思想》扣除押金)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户积分流水明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ==============================================
-- Table: t_address
-- ==============================================
--
-- Table structure for table `t_address`
--

DROP TABLE IF EXISTS `t_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_address` (
  `aid` int NOT NULL AUTO_INCREMENT COMMENT '收货地址id',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(20) DEFAULT '' COMMENT '收货人姓名',
  `province_name` varchar(15) DEFAULT '' COMMENT '省-名称',
  `city_name` varchar(15) DEFAULT '' COMMENT '市-名称',
  `address` varchar(50) DEFAULT '' COMMENT '详细地址',
  `phone` varchar(20) DEFAULT '' COMMENT '手机',
  `tag` varchar(6) DEFAULT '' COMMENT '标签',
  `is_default` int DEFAULT '0' COMMENT '是否默认：0-不默认，1-默认',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modified_user` varchar(20) DEFAULT '' COMMENT '修改人',
  `modified_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`aid`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='收货地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ==============================================
-- Table: t_cart
-- ==============================================
--
-- Table structure for table `t_cart`
--

DROP TABLE IF EXISTS `t_cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_cart` (
  `cart_id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车数据id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `cart_books_id` bigint NOT NULL COMMENT '图书id',
  `cart_num` int DEFAULT '0' COMMENT '商品数量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`cart_id`),
  KEY `idx_cart_user` (`user_id`),
  KEY `idx_cart_books` (`cart_books_id`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8 COMMENT='借阅车表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ==============================================
-- Table: t_message
-- ==============================================
--
-- Table structure for table `t_message`
--

DROP TABLE IF EXISTS `t_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_message` (
  `msg_id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息主键',
  `sender_id` bigint DEFAULT '0' COMMENT '发送者ID (0代表系统通知)',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID (通知给谁)',
  `msg_type` char(1) NOT NULL DEFAULT '1' COMMENT '消息类型 (1:订单通知 2:系统公告 3:互动提醒)',
  `title` varchar(100) NOT NULL COMMENT '消息标题 (例如：新的借阅申请)',
  `content` varchar(500) NOT NULL COMMENT '消息正文 (例如：用户XX申请借阅您的《老子的海》)',
  `related_id` bigint DEFAULT NULL COMMENT '关联业务ID (例如存入 order_id，方便前端点击跳转到详情)',
  `is_read` char(1) DEFAULT '0' COMMENT '阅读状态 (0:未读 1:已读)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`msg_id`),
  KEY `idx_receiver` (`receiver_id`,`is_read`) COMMENT '优化查询某人的未读消息'
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息提醒表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ==============================================
-- Table: t_order
-- ==============================================
--
-- Table structure for table `t_order`
--

DROP TABLE IF EXISTS `t_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_order` (
  `order_id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `borrower_id` bigint NOT NULL COMMENT '借阅者ID',
  `owner_id` bigint NOT NULL COMMENT '书主ID',
  `order_recv_name` varchar(20) NOT NULL COMMENT '收货人姓名',
  `order_recv_phone` varchar(20) DEFAULT '' COMMENT '收货人电话',
  `order_recv_province` varchar(15) DEFAULT '' COMMENT '省份',
  `order_recv_city` varchar(15) DEFAULT '' COMMENT '城市',
  `order_recv_address` varchar(50) DEFAULT '' COMMENT '详细地址',
  `order_status` char(2) DEFAULT '0' COMMENT '订单状态',
  `order_time` datetime DEFAULT NULL COMMENT '下单时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `consume_points` int DEFAULT '0' COMMENT '本次借阅消耗的租金积分(如书价的10%)',
  `pledge_points` int DEFAULT '0' COMMENT '本次借阅冻结的押金积分(如书价的100%)',
  `reward_points` int DEFAULT '0' COMMENT '图书提供者预计可获得的收益积分(如租金的80%)',
  PRIMARY KEY (`order_id`),
  KEY `idx_borrower` (`borrower_id`),
  KEY `idx_owner` (`owner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='借阅订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ==============================================
-- Table: t_order_item
-- ==============================================
--
-- Table structure for table `t_order_item`
--

DROP TABLE IF EXISTS `t_order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_order_item` (
  `order_item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单明细ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `book_id` bigint NOT NULL COMMENT '书籍ID',
  `book_title` varchar(100) NOT NULL COMMENT '书籍标题',
  `book_image` varchar(500) DEFAULT '' COMMENT '书籍图片',
  `book_num` int DEFAULT '1' COMMENT '数量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`order_item_id`),
  KEY `idx_item_order` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='借阅订单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'ry'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-16 13:52:04

-- 共提取业务表 9 张