-- MySQL dump 10.13  Distrib 5.7.28, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: plant_db
-- ------------------------------------------------------
-- Server version	8.0.25

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (1,'admin','$2a$10$ilJNZA0a5Wt0vyCItxWMXuHEnqCHw/2VI8zMJwXyL7Cd1ZAlTwHKS','1656467387@qq.com','15630831823','http://localhost:8080/api/files/download/c98d1372-e621-4b8b-92dd-7c12e69ebdf2.jpg','2025-06-09 16:33:29');
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `user_type` enum('normal','expert') NOT NULL,
  `target_type` enum('plant','pest_disease','pesticide') NOT NULL,
  `target_id` int NOT NULL,
  `content` text NOT NULL,
  `status` enum('pending','approved','rejected') DEFAULT 'pending',
  `review_admin_id` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `review_admin_id` (`review_admin_id`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`review_admin_id`) REFERENCES `admin` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,2,'normal','plant',12,'我的第一个评论','approved',1,'2025-06-11 11:09:20'),(2,2,'normal','pest_disease',19,'我的第二个评论','rejected',1,'2025-06-11 11:09:53'),(3,2,'normal','pesticide',5,'我的第三个评论','approved',1,'2025-06-11 11:10:05'),(4,1,'normal','plant',12,'菊花啊啊啊啊啊','approved',1,'2025-06-11 11:18:42'),(5,2,'normal','pest_disease',13,'这是什么病','approved',1,'2025-06-11 11:21:09'),(6,2,'normal','pest_disease',19,'潜叶蛾病','pending',NULL,'2025-06-09 11:27:13'),(7,2,'normal','pesticide',3,'氯氰菊酯杀虫剂','approved',1,'2025-06-10 11:27:58'),(8,2,'normal','pesticide',6,'吡虫啉能喝吗？','approved',1,'2025-06-10 14:26:21'),(9,3,'normal','plant',12,'巨巨巨','approved',1,'2025-06-19 11:04:55'),(10,1,'normal','pest_disease',19,'我勒个豆','approved',1,'2025-06-19 11:27:59');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `disease_pesticide_link`
--

DROP TABLE IF EXISTS `disease_pesticide_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `disease_pesticide_link` (
  `id` int NOT NULL AUTO_INCREMENT,
  `pd_id` int NOT NULL,
  `pesticide_id` int NOT NULL,
  `effectiveness` enum('高','中','低') DEFAULT '中',
  `usage_dosage` varchar(100) DEFAULT NULL,
  `application_method` varchar(100) DEFAULT NULL,
  `safe_interval_days` int DEFAULT NULL,
  `side_effects` text,
  `notes` text,
  `created_by` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `pd_id` (`pd_id`),
  KEY `pesticide_id` (`pesticide_id`),
  KEY `created_by` (`created_by`),
  CONSTRAINT `disease_pesticide_link_ibfk_1` FOREIGN KEY (`pd_id`) REFERENCES `pest_disease` (`id`) ON DELETE CASCADE,
  CONSTRAINT `disease_pesticide_link_ibfk_2` FOREIGN KEY (`pesticide_id`) REFERENCES `pesticide` (`id`) ON DELETE CASCADE,
  CONSTRAINT `disease_pesticide_link_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `expert_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `disease_pesticide_link`
--

LOCK TABLES `disease_pesticide_link` WRITE;
/*!40000 ALTER TABLE `disease_pesticide_link` DISABLE KEYS */;
INSERT INTO `disease_pesticide_link` VALUES (1,19,6,'高','每亩用200ml','页面喷雾',4,'对植物有些许影响','注意',NULL,'2025-06-09 15:00:55'),(2,19,5,'中','测试','测试',1,'测试','测试',1,'2025-06-09 15:21:18'),(3,19,3,'高','搜索','叶面喷雾',5,'副作用都有','药效高，副作用大',2,'2025-06-09 15:24:44'),(4,14,6,'高','每亩用量80毫升','页面喷雾',3,'可能副作用','杀死蝗虫',2,'2025-06-11 14:28:40'),(5,13,4,'高','','',0,'','',2,'2025-06-11 15:05:09');
/*!40000 ALTER TABLE `disease_pesticide_link` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `expert_user`
--

DROP TABLE IF EXISTS `expert_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `expert_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `organization` varchar(255) DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `expert_user`
--

LOCK TABLES `expert_user` WRITE;
/*!40000 ALTER TABLE `expert_user` DISABLE KEYS */;
INSERT INTO `expert_user` VALUES (1,'专家1','$2a$10$sLx6kl2PxJ9XhvoUQZHt5eHwsey1pM/IpPQEVtRuCso1P8qhGD46.','1656467387@qq.coy','15075109602','张三','高级','河北农业大学','http://localhost:8080/api/files/download/6bc7c4c6-32e7-4a4c-8db6-fef1a445f7db_user1.jpg','2025-06-06 17:59:05','2025-06-21 22:42:26'),(2,'专家2','$2a$10$153090HOw1u07sCWAe3tX.x/Kl/rzJsT3LwAR5i1YdUtaQjNL9c3y','zhou@qq.com','15075109602','李四','中级','河北农业大学','http://localhost:8080/api/files/download/91aef5a7-604c-4ff0-9894-45695c6849e5_user7.jpg','2025-06-09 15:23:19','2025-06-12 09:41:28'),(3,'专家3','$2a$10$lSzt.PNp1pD3fl70KprfteLaot67o2rgIybZdJhB6D39rEcK4Mbg2','41565001@qq.com','19933823736','王五','低级','河北农业大学','http://localhost:8080/api/files/download/1512bb17-0209-42cf-95c6-43d021740920_user4.jpg','2025-06-09 16:30:45','2025-06-12 09:29:13');
/*!40000 ALTER TABLE `expert_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `help_reply`
--

DROP TABLE IF EXISTS `help_reply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `help_reply` (
  `id` int NOT NULL AUTO_INCREMENT,
  `help_id` int NOT NULL,
  `expert_id` int NOT NULL,
  `content` text NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `help_id` (`help_id`),
  KEY `expert_id` (`expert_id`),
  CONSTRAINT `help_reply_ibfk_1` FOREIGN KEY (`help_id`) REFERENCES `help_request` (`id`) ON DELETE CASCADE,
  CONSTRAINT `help_reply_ibfk_2` FOREIGN KEY (`expert_id`) REFERENCES `expert_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `help_reply`
--

LOCK TABLES `help_reply` WRITE;
/*!40000 ALTER TABLE `help_reply` DISABLE KEYS */;
INSERT INTO `help_reply` VALUES (1,3,1,'回复1','2025-06-10 16:55:53'),(2,3,1,'回复2','2025-06-11 08:48:59'),(3,3,1,'回复3','2025-06-11 08:56:11'),(4,3,1,'回复4','2025-06-11 08:58:26'),(5,3,1,'回复5','2025-06-11 09:04:32'),(6,3,1,'回复6','2025-06-11 09:06:08'),(7,4,1,'回复7','2025-06-11 09:11:05'),(8,4,1,'回复8','2025-06-11 09:12:15'),(9,4,1,'回复9','2025-06-11 09:13:31'),(10,4,1,'回复10','2025-06-11 09:16:37'),(11,4,1,'回复11','2025-06-11 09:18:43'),(12,4,3,'回复12','2025-06-11 09:22:31');
/*!40000 ALTER TABLE `help_reply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `help_request`
--

DROP TABLE IF EXISTS `help_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `help_request` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `title` varchar(200) DEFAULT NULL,
  `description` text,
  `image_url` varchar(255) DEFAULT NULL,
  `status` enum('pending','approved','rejected') DEFAULT 'pending',
  `review_admin_id` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `review_admin_id` (`review_admin_id`),
  CONSTRAINT `help_request_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `normal_user` (`id`),
  CONSTRAINT `help_request_ibfk_2` FOREIGN KEY (`review_admin_id`) REFERENCES `admin` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `help_request`
--

LOCK TABLES `help_request` WRITE;
/*!40000 ALTER TABLE `help_request` DISABLE KEYS */;
INSERT INTO `help_request` VALUES (3,1,'测试求助1','test help 1','','approved',1,'2025-06-10 16:15:05'),(4,1,'测试求助2','test help 2','','approved',1,'2025-06-10 17:17:35'),(5,2,'测试求助3','test help 3','','rejected',1,'2025-06-11 09:43:20'),(6,1,'测试求助4','test help 4','','pending',NULL,'2025-06-11 16:27:06'),(7,1,'测试求助5','test help 5','','pending',NULL,'2025-06-11 17:18:43'),(8,1,'测试求助6','test help 6','','pending',NULL,'2025-06-12 08:52:16'),(9,1,'test7','测试求助7',NULL,'pending',NULL,'2025-06-18 17:20:43'),(10,4,'测试求助8','test help 8',NULL,'pending',NULL,'2025-06-19 08:47:23'),(11,1,'测试求助11','test11','http://localhost:8080/api/files/download/2ae2fcaa-9c81-4c84-91ac-85aa32b72d37.jpg','approved',1,'2025-06-19 09:04:11');
/*!40000 ALTER TABLE `help_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medicinal_plant`
--

DROP TABLE IF EXISTS `medicinal_plant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medicinal_plant` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `description` text,
  `medicinal_parts` text,
  `efficacy` text,
  `image_url` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` int DEFAULT NULL,
  `view_count` int DEFAULT '0' COMMENT '查看次数',
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  CONSTRAINT `medicinal_plant_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `expert_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicinal_plant`
--

LOCK TABLES `medicinal_plant` WRITE;
/*!40000 ALTER TABLE `medicinal_plant` DISABLE KEYS */;
INSERT INTO `medicinal_plant` VALUES (2,'人参','黄精','多年生草本植物，主根肉质，呈圆柱形或纺锤形，表皮淡黄色。茎直立，叶轮生，伞形花序顶生，果实为扁球形红色浆果。喜阴凉湿润气候，多生长于海拔数百米的落叶阔叶林或针阔混交林下。','root,stem','大补元气、复脉固脱、补脾益肺、生津养血、安神益智。常用于体虚欲脱、肢冷脉微、脾虚食少、肺虚喘咳、津伤口渴、内热消渴、气血亏虚、久病虚羸、惊悸失眠等症。','http://localhost:8080/api/files/download/bf35ef35-000a-41ba-a808-b96a83575d68.jpg','2025-06-06 18:06:58','2025-06-19 11:05:34',1,12),(3,'黄芪','棉芪','多年生草本，茎直立，上部多分枝。奇数羽状复叶，小叶椭圆形。总状花序，花黄色或淡黄色。荚果薄膜质，半椭圆形。多生长于林缘、灌丛或疏林下，亦见于山坡草地或草甸中。','root','补气升阳、固表止汗、利水消肿、生津养血、行滞通痹、托毒排脓、敛疮生肌。用于气虚乏力、食少便溏、中气下陷、久泻脱肛、便血崩漏、表虚自汗、气虚水肿、内热消渴、血虚萎黄、半身不遂、痹痛麻木、痈疽难溃、久溃不敛等。','http://localhost:8080/api/files/download/1d4d401d-bf4d-4763-96b3-fd19ab74c7f4.jpg','2025-06-06 18:34:31','2025-06-19 11:02:30',1,2),(4,'当归','干归','多年生草本，全株有香气。根肥大，肉质，表面黄棕色至深褐色。茎带紫色，基生叶及茎下部叶卵形，边缘有锯齿。复伞形花序，花白色。双悬果椭圆形，侧棱有翅。多生于高寒多雨山区的草坡或林下。','root','补血活血、调经止痛、润肠通便。用于血虚萎黄、眩晕心悸、月经不调、经闭痛经、虚寒腹痛、风湿痹痛、跌扑损伤、痈疽疮疡、肠燥便秘等。','http://localhost:8080/api/files/download/6da526d8-fd11-4c56-af82-0e041aa76716.jpg','2025-06-06 18:54:14','2025-06-19 11:02:19',1,1),(5,'枸杞','杞子','茄科灌木，枝条细弱，弓状弯曲或俯垂。叶互生或簇生，卵形或长椭圆形。花单生或 2-4 朵簇生于叶腋，淡紫色。浆果红色，卵状或长椭圆状，种子扁肾形。适应性强，多生长于荒地、丘陵、盐碱地等地。','root,fruit','滋补肝肾、益精明目。用于虚劳精亏、腰膝酸痛、眩晕耳鸣、内热消渴、血虚萎黄、目昏不明。','http://localhost:8080/api/files/download/1c2c8110-7ef7-485b-a40f-2e194fe4d071.jpg','2025-06-06 18:54:43','2025-06-19 11:02:12',1,4),(6,'甘草','甜草','多年生草本，根与根状茎粗壮，外皮褐色，里面淡黄色。茎直立，多分枝。奇数羽状复叶，小叶卵形或长卵形。总状花序腋生，花紫色、白色或黄色。荚果弯曲呈镰刀状或环状，密被褐色腺点及短柔毛。多生长于干旱、半干旱的沙土、沙漠边缘和黄土丘陵地带。','root,stem','补脾益气、清热解毒、祛痰止咳、缓急止痛、调和诸药。用于脾胃虚弱、倦怠乏力、心悸气短、咳嗽痰多、痈肿疮毒、咽喉肿痛、药食中毒、脘腹、四肢挛急疼痛等。并能缓和药物烈性、毒性，协调诸药药性。','http://localhost:8080/api/files/download/e7d59241-8499-42a6-aa9b-a3cf34136468.jpg','2025-06-06 18:55:03','2025-06-19 11:02:03',1,1),(7,'黄连','味连','多年生草本，根茎簇生，形如鸡爪，表面灰黄色，粗糙有节。叶基生，具长柄，叶片稍带革质，卵状三角形，三全裂。花葶 1-2 条，顶生聚伞花序，花小，黄绿色。蓇葖果长圆形。多生于山地林中或山谷阴处，喜冷凉、湿润环境。','root,stem','清热燥湿、泻火解毒。用于湿热痞满、呕吐吞酸、泻痢、黄疸、高热神昏、心火亢盛、心烦不寐、血热吐衄、目赤、牙痛、消渴、痈肿疔疮；外治湿疹、湿疮、耳道流脓。','http://localhost:8080/api/files/download/6a11bbd0-6534-49da-a947-085134690a0a.jpg','2025-06-06 18:55:43','2025-06-19 16:34:37',1,2),(8,'茯苓','茯菟','多孔菌科真菌，寄生在松树根上的多年生菌类。菌核呈球形、卵形或不规则块状，表面粗糙，棕褐色至黑褐色，内部白色或淡粉红色。子实体伞状，生于菌核表面。多生长于海拔 200-1500 米的松树根际土壤中。','fruit','利水渗湿、健脾、宁心。用于水肿尿少、痰饮眩悸、脾虚食少、便溏泄泻、心神不安、惊悸失眠。','http://localhost:8080/api/files/download/ee4e7b2f-bce3-4e4e-96bf-e255aa822632.jpg','2025-06-06 18:56:05','2025-06-19 11:01:37',1,1),(9,' 麻黄','龙沙','多年生草本状小灌木，茎直立或匍匐，分枝较少，节明显，表面黄绿色至淡绿色，有细纵槽。叶鳞片状，膜质，基部鞘状。雌雄异株，雄球花淡黄绿色，雌球花苞片肉质，红色。多生长于干旱山地及荒漠中，耐干旱、耐贫瘠。','stem','发汗解表、宣肺平喘、利水消肿。用于风寒感冒、胸闷喘咳、风水浮肿；支气管哮喘。蜜炙麻黄润肺止咳，多用于表证已解、气喘咳嗽。','http://localhost:8080/api/files/download/e5589c26-de0c-4043-bbad-99b41472bc81.jpg','2025-06-06 18:56:26','2025-06-19 11:01:28',1,2),(10,'柴胡','地熏','多年生草本，主根较粗，坚硬，表面红棕色或黑棕色。茎直立，实心，上部分枝。叶互生，披针形或长圆状披针形，有平行脉。复伞形花序，花黄色。双悬果长圆形，棱槽中具油管。多生于草原、山坡草地、路边及林缘。','root','疏散退热、疏肝解郁、升举阳气。用于感冒发热、寒热往来、胸胁胀痛、月经不调、子宫脱垂、脱肛。','http://localhost:8080/api/files/download/0ab3ef20-a234-4ff2-8401-f0102402c706.jpg','2025-06-06 18:57:00','2025-06-19 11:01:13',1,6),(11,'薄荷','蕃荷菜','多年生草本，茎直立，下部数节具纤细的须根及水平匍匐根状茎，上部多分枝，全株有清凉香气。叶对生，卵形或长圆形，边缘有锯齿，两面均有柔毛。轮伞花序腋生，花小，淡紫色。小坚果卵珠形，黄褐色。多生于河旁、水沟边或潮湿地，适应性较强。','stem,leaf','疏散风热、清利头目、利咽透疹、疏肝行气。用于风热感冒、风温初起、头痛、目赤、喉痹、口疮、风疹、麻疹、胸胁胀闷。','http://localhost:8080/api/files/download/38476ced-1970-4419-884e-61e12e39090f.jpg','2025-06-06 18:57:22','2025-06-21 22:33:31',1,11),(12,'菊花','寿客','多年生宿根草本，茎直立，分枝或不分枝，被柔毛。叶互生，卵形至披针形，边缘有粗大锯齿或深裂。头状花序单生或数个集生于茎顶，花色多样（黄、白、紫等），舌状花为雌花，管状花为两性花。多生长于丘陵、山坡、草地或林缘，喜阳光充足、温暖湿润环境。','flower','疏散风热，用于风热感冒、发热头痛。','http://localhost:8080/api/files/download/ea674ec3-484e-44ab-a179-2cf022aa23eb.jpg','2025-06-06 18:57:45','2025-06-21 22:42:40',1,104);
/*!40000 ALTER TABLE `medicinal_plant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `normal_user`
--

DROP TABLE IF EXISTS `normal_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `normal_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `nickname` varchar(100) DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `normal_user`
--

LOCK TABLES `normal_user` WRITE;
/*!40000 ALTER TABLE `normal_user` DISABLE KEYS */;
INSERT INTO `normal_user` VALUES (1,'月亮啊','$2a$10$9V2PCL0.k8fCHnpsEdAwSOYhC6hNtErLyIsDoUavQ4docEslxQwAC','1656467387@qq.com','15630831870','韩佳烜','http://localhost:8080/api/files/download/6366186f-ab08-4128-a008-f15d4b38f28a_user2.jpg','2025-06-06 20:59:18'),(2,'太阳啊','$2a$10$S17t3VnJ69a91r8GZYZ6/e7h2YqgWl1.GSjLwAUJY/R9Dq1IK0oki','1656@qq.com','15075109602','我是大魔王','http://localhost:8080/api/files/download/a1ddcee2-86ec-4c2a-9e8a-bf7cabf3eb3d_user6.jpg','2025-06-11 09:32:25'),(3,'星星啊','$2a$10$3FP4fwJaUzS0xfoZQdlc8OxiUfsidHgSv3zobMyA5Z49ZuSDePsrO',NULL,NULL,'一闪一闪亮晶晶','http://localhost:8080/api/files/download/d0920522-2e5d-412d-8a11-9f1c7a96f64e.jpg','2025-06-12 09:39:35'),(4,'芬芬啊','$2a$10$YFRLzIc1ElWd4CBO82oS5ei8aLeO0hCB7y/8k1QN5Cg1ltl7jO5vy','fen@123.com','15673827442','一笑倾城','http://localhost:8080/api/files/download/e9f16a8c-7c15-42d2-a1e2-db00d76c0a07.jpg','2025-06-19 08:40:19');
/*!40000 ALTER TABLE `normal_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pest_disease`
--

DROP TABLE IF EXISTS `pest_disease`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pest_disease` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `type` enum('病害','虫害') NOT NULL,
  `description` text,
  `symptoms` text,
  `image_url` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` int DEFAULT NULL,
  `view_count` int DEFAULT '0' COMMENT '查看次数',
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  CONSTRAINT `pest_disease_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `expert_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pest_disease`
--

LOCK TABLES `pest_disease` WRITE;
/*!40000 ALTER TABLE `pest_disease` DISABLE KEYS */;
INSERT INTO `pest_disease` VALUES (1,'黑星病','病害','是一种由真菌引起的病害，常发生于多种果树和蔬菜上。','果实、叶片和新梢等部位均可受害。果实上出现黑色圆形病斑，大小不一，后期病斑凹陷，表面有黑色霉层；叶片上病斑呈圆形或不规则形，黑色，周围有黄色晕圈。','http://localhost:8080/api/files/download/8cf714a0-33cc-423d-ad5f-9580337d0bae_黑星病.jpg','2025-06-06 22:13:37','2025-06-10 17:11:05',1,0),(8,'灰霉病','病害','由灰葡萄孢菌引起，是一种常见的低温高湿型病害，能侵染多种植物。','发病部位先出现水渍状斑点，后逐渐扩大，病斑上长出灰色霉层，严重时病斑连片，导致组织腐烂。','http://localhost:8080/api/files/download/31b10773-c415-47c9-958b-5712c4bb60d8_灰霉病.jpg','2025-06-06 22:17:41','2025-06-10 17:10:53',1,1),(9,'叶斑病','病害','是一类常见的叶部病害，由多种真菌或细菌引起。','叶片上出现各种形状、颜色的病斑，如圆形、椭圆形、不规则形等，颜色有褐色、黑色、灰色等，病斑周围常有黄色晕圈，严重时叶片枯黄脱落。','http://localhost:8080/api/files/download/33de71d2-bc45-4c72-8154-e5bfc41b5567_叶斑病.jpg','2025-06-06 22:18:54','2025-06-18 16:52:43',1,1),(10,'枯萎病','病害','由真菌或细菌引起，主要危害植物的维管束系统。','植株从下部叶片开始发黄、萎蔫，逐渐向上发展，最后整株枯萎死亡。茎基部常出现褐色病斑，维管束变为褐色。','http://localhost:8080/api/files/download/b76d95ed-5494-4a6c-857a-2cac73b23e3d_枯萎病.jpg','2025-06-06 22:19:15','2025-06-18 16:51:04',1,1),(11,'炭疽叶枯病','病害','由炭疽菌引起的叶部病害，常发生于高温高湿环境下的果树和园林植物。','叶片上迅速出现圆形或近圆形的褐色病斑，病斑中央灰白色，边缘红褐色，有明显的轮纹，后期病斑上出现黑色小点。','http://localhost:8080/api/files/download/d805be91-6da7-401c-9179-c584f2e98a5e_炭疽叶枯病.jpg','2025-06-06 22:19:26','2025-06-10 17:10:16',1,0),(12,'病毒病','病害','由病毒引起，通过昆虫、汁液接触等方式传播，能侵染多种植物。','表现为花叶、黄化、皱缩、畸形、矮化等，叶片颜色不均，出现黄绿相间的斑驳，植株生长不良，果实发育受阻。','http://localhost:8080/api/files/download/06b1952a-d9a5-4b04-8db5-c15b581e2eef_病毒病.jpg','2025-06-06 22:19:39','2025-06-18 16:56:40',1,2),(13,'细菌性软腐病','病害','由细菌引起，多发生于植物的多汁器官，如根茎、果实等。','受害部位初呈水渍状，后逐渐变软腐烂，有恶臭味，病组织变为褐色糊状。','http://localhost:8080/api/files/download/df86da47-e5b2-41e0-828a-cbf89c8ee03c_细菌性软腐病.jpg','2025-06-06 22:19:52','2025-06-21 22:42:43',1,27),(14,' 蝗虫','虫害','直翅目昆虫，种类繁多，是一种食叶性害虫，常大量聚集，对农作物、草原和森林造成严重危害。','成虫和若虫均以植物叶片为食，会将叶片咬成缺刻或吃光，严重时导致大面积植物枯黄、死亡。','http://localhost:8080/api/files/download/fc2d6a8e-2d83-4da6-a97c-1150b782c497_蝗虫.jpg','2025-06-06 22:20:04','2025-06-19 16:39:16',1,11),(15,'蝼蛄','虫害','地下害虫，前足特化为开掘足，善于在土壤中挖掘隧道，取食植物的种子、幼苗和根部。','在苗床或田间，蝼蛄会将幼苗根部咬断，使幼苗死亡，同时其挖掘的隧道会导致幼苗根部与土壤分离，影响幼苗生长。','http://localhost:8080/api/files/download/f5838410-8411-4985-9aef-7bcdc3e950c1_蝼蛄.jpg','2025-06-06 22:20:14','2025-06-19 10:26:00',1,2),(16,' 金针虫','虫害','是叩头虫的幼虫，身体细长，呈金黄色或棕褐色，生活在土壤中，主要危害植物的根部、茎基部和种子。','幼虫咬食种子胚乳和幼苗根部，造成缺苗断垄，受害根部呈丝状，易与其他地下害虫危害症状区分。','http://localhost:8080/api/files/download/a37610a4-a8b4-42e4-b352-fd1a7f219300_金针虫.jpg','2025-06-06 22:20:26','2025-06-10 17:09:25',1,0),(17,'蓟马','虫害','体型微小，多为黑色或褐色，锉吸式口器，喜欢在花中或叶片背面活动，能危害多种植物。','以锉吸植物汁液为主，导致叶片出现灰白色斑点，严重时叶片卷曲、皱缩，花朵受害后，会出现畸形、变色，影响植物的观赏价值和结实率。','http://localhost:8080/api/files/download/b338e61f-7888-4344-9a7e-e5071de0d014_蓟马.jpg','2025-06-06 22:20:38','2025-06-18 16:50:54',1,10),(18,'叶螨','虫害','又称红蜘蛛，体型较小，通常为红色或褐色，以刺吸式口器吸食植物汁液，多在叶片背面活动。','叶片上出现黄白色小点，随着危害加重，小点连成一片，叶片失绿、枯黄，严重时叶片脱落，植株生长受到抑制。','http://localhost:8080/api/files/download/805ba975-5777-4986-a461-be7d349a443e_叶螨.jpg','2025-06-06 22:20:48','2025-06-21 22:33:33',1,9),(19,'潜叶蛾','虫害','幼虫潜入叶片表皮下取食叶肉，形成弯曲的虫道，主要危害果树和蔬菜的叶片。','叶片上可见白色或褐色的蜿蜒虫道，使叶片卷曲、皱缩，影响叶片的光合作用，严重时导致叶片脱落。','http://localhost:8080/api/files/download/3492c34e-3f75-4ae5-9f63-411613fae1e4.jpg','2025-06-06 22:20:58','2025-06-21 22:43:01',1,49);
/*!40000 ALTER TABLE `pest_disease` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pesticide`
--

DROP TABLE IF EXISTS `pesticide`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pesticide` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `active_ingredient` varchar(255) DEFAULT NULL,
  `usage_instructions` text,
  `image_url` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` int DEFAULT NULL,
  `view_count` int DEFAULT '0' COMMENT '查看次数',
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  CONSTRAINT `pesticide_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `expert_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pesticide`
--

LOCK TABLES `pesticide` WRITE;
/*!40000 ALTER TABLE `pesticide` DISABLE KEYS */;
INSERT INTO `pesticide` VALUES (3,'氯氰菊酯','杀虫剂','氯氰菊酯。','可用于防治多种害虫，如菜青虫、蚜虫、棉铃虫等。一般用2.5%乳油2000 - 3000倍液喷雾，根据害虫发生情况，间隔7 - 10天可再喷一次。注意对蜜蜂、鱼类等有毒，使用时避免污染水源和蜜源植物。','http://localhost:8080/api/files/download/3e4980db-2bb9-42d4-82b9-954090c36f56_氯氰菊酯.jpg','2025-06-07 00:44:22','2025-06-19 14:24:12',1,5),(4,'百菌清','杀菌剂','百菌清。','主要用于防治多种作物的真菌病害，如番茄早疫病、瓜类霜霉病等。通常用75%可湿性粉剂600 - 800倍液喷雾，在病害发生初期开始用药，每隔7 - 10天喷一次，连续喷2 - 3次。不能与强碱性农药混用。','http://localhost:8080/api/files/download/ddae4573-b59d-479a-8400-6175594fd1bb_百菌清.jpg','2025-06-07 00:44:53','2025-06-19 15:54:34',1,5),(5,'草甘膦','除草剂','草甘膦。','用于防除一年生和多年生杂草。一般用41%水剂100 - 200倍液，定向喷雾于杂草茎叶上。喷药时要防止药液飘移到附近作物上，施药后3 - 5天杂草开始变黄，7 - 10天逐渐死亡。','http://localhost:8080/api/files/download/6608d3f3-f23f-4304-8ead-2bde94292a89_草甘膦.jpg','2025-06-07 00:45:06','2025-06-19 14:24:08',1,6),(6,'吡虫啉','杀虫剂','吡虫啉。','可防治蚜虫、飞虱、蓟马等害虫。常用10%可湿性粉剂1000 - 1500倍液喷雾。可与多种农药混用，但不能与碱性农药混用。对蜜蜂有毒，花期禁用。','http://localhost:8080/api/files/download/afee45c3-aab2-4790-a6b8-b769989e2048.jpg','2025-06-07 00:45:21','2025-06-21 22:40:08',1,16);
/*!40000 ALTER TABLE `pesticide` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `plant_disease_link`
--

DROP TABLE IF EXISTS `plant_disease_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `plant_disease_link` (
  `id` int NOT NULL AUTO_INCREMENT,
  `plant_id` int NOT NULL,
  `pd_id` int NOT NULL,
  `vulnerability` enum('高','中','低') DEFAULT '中',
  `occurrence_season` varchar(100) DEFAULT NULL,
  `affected_parts` varchar(255) DEFAULT NULL,
  `notes` text,
  `created_by` int DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `plant_id` (`plant_id`),
  KEY `pd_id` (`pd_id`),
  KEY `created_by` (`created_by`),
  CONSTRAINT `plant_disease_link_ibfk_1` FOREIGN KEY (`plant_id`) REFERENCES `medicinal_plant` (`id`) ON DELETE CASCADE,
  CONSTRAINT `plant_disease_link_ibfk_2` FOREIGN KEY (`pd_id`) REFERENCES `pest_disease` (`id`) ON DELETE CASCADE,
  CONSTRAINT `plant_disease_link_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `expert_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `plant_disease_link`
--

LOCK TABLES `plant_disease_link` WRITE;
/*!40000 ALTER TABLE `plant_disease_link` DISABLE KEYS */;
INSERT INTO `plant_disease_link` VALUES (1,12,17,'中','春秋季','叶片','菊花',1,'2025-06-09 14:58:17'),(2,11,14,'低','夏季','叶片','测试',1,'2025-06-09 14:59:20'),(3,10,13,'高','秋季','根部','测试',1,'2025-06-09 14:59:48'),(4,11,18,'低','春季','叶片','测试',1,'2025-06-09 15:20:48'),(5,4,18,'中','春季','叶片','测试',2,'2025-06-09 15:23:56'),(6,12,14,'高','春季','根部','这是菊花的蝗虫',2,'2025-06-11 10:47:51'),(7,12,13,'中','夏季','叶片','菊花病',2,'2025-06-11 10:48:29');
/*!40000 ALTER TABLE `plant_disease_link` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-21 22:45:54
