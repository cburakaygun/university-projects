-- MySQL dump 10.13  Distrib 5.7.22, for Linux (x86_64)
--
-- Host: localhost    Database: proj_man_db
-- ------------------------------------------------------
-- Server version	5.7.22-0ubuntu0.16.04.1

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
-- Table structure for table `ar_internal_metadata`
--

DROP TABLE IF EXISTS `ar_internal_metadata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ar_internal_metadata` (
  `key` varchar(255) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ar_internal_metadata`
--

LOCK TABLES `ar_internal_metadata` WRITE;
/*!40000 ALTER TABLE `ar_internal_metadata` DISABLE KEYS */;
INSERT INTO `ar_internal_metadata` VALUES ('environment','development','2018-05-07 14:24:50','2018-05-07 14:24:50');
/*!40000 ALTER TABLE `ar_internal_metadata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employees`
--

DROP TABLE IF EXISTS `employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `employees` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees`
--

LOCK TABLES `employees` WRITE;
/*!40000 ALTER TABLE `employees` DISABLE KEYS */;
INSERT INTO `employees` VALUES (1,'Employee','One','2018-05-07 14:36:18','2018-05-07 14:36:18'),(2,'Employee','Two','2018-05-07 14:36:24','2018-05-07 14:36:24'),(3,'Employee','Three','2018-05-07 14:36:29','2018-05-07 14:36:29'),(4,'Employee','Four','2018-05-07 14:36:34','2018-05-07 14:36:34'),(5,'Employee','Five','2018-05-07 14:36:39','2018-05-07 14:36:39'),(6,'Employee','Six','2018-05-07 14:36:44','2018-05-07 14:36:44'),(7,'Employee','Seven','2018-05-07 14:36:50','2018-05-07 14:36:50'),(8,'Employee','Eight','2018-05-07 14:37:04','2018-05-07 14:37:04');
/*!40000 ALTER TABLE `employees` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,NO_AUTO_VALUE_ON_ZERO,STRICT_TRANS_TABLES,STRICT_ALL_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER del_task_assignment BEFORE DELETE ON employees
      FOR EACH ROW DELETE FROM task_assignments WHERE employee_id = OLD.id */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `project_assignments`
--

DROP TABLE IF EXISTS `project_assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_assignments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `project_manager_id` bigint(20) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_project_assignments_on_project_id` (`project_id`),
  KEY `index_project_assignments_on_project_manager_id` (`project_manager_id`),
  CONSTRAINT `fk_rails_9454cf0c8d` FOREIGN KEY (`project_manager_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_rails_b8185b0c0e` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project_assignments`
--

LOCK TABLES `project_assignments` WRITE;
/*!40000 ALTER TABLE `project_assignments` DISABLE KEYS */;
INSERT INTO `project_assignments` VALUES (1,3,2,'2018-05-07 14:51:27','2018-05-07 14:51:27'),(2,5,2,'2018-05-07 14:51:32','2018-05-07 14:51:32'),(3,7,2,'2018-05-07 14:51:35','2018-05-07 14:51:35'),(4,9,2,'2018-05-07 14:51:39','2018-05-07 14:51:39'),(5,11,2,'2018-05-07 14:51:42','2018-05-07 14:51:42'),(6,2,3,'2018-05-07 14:52:10','2018-05-07 14:52:10'),(7,3,3,'2018-05-07 14:52:14','2018-05-07 14:52:14'),(8,5,3,'2018-05-07 14:52:17','2018-05-07 14:52:17'),(9,7,3,'2018-05-07 14:52:20','2018-05-07 14:52:20'),(10,11,3,'2018-05-07 14:52:27','2018-05-07 14:52:27'),(11,2,4,'2018-05-07 14:53:10','2018-05-07 14:53:10'),(12,4,4,'2018-05-07 14:53:13','2018-05-07 14:53:13'),(13,6,4,'2018-05-07 14:53:16','2018-05-07 14:53:16'),(14,8,4,'2018-05-07 14:53:20','2018-05-07 14:53:20'),(15,10,4,'2018-05-07 14:53:22','2018-05-07 14:53:22'),(16,12,4,'2018-05-07 14:53:26','2018-05-07 14:53:26'),(17,3,4,'2018-05-07 14:53:51','2018-05-07 14:53:51');
/*!40000 ALTER TABLE `project_assignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `projects`
--

DROP TABLE IF EXISTS `projects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `projects` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `start_date` date NOT NULL,
  `task_days` int(11) NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projects`
--

LOCK TABLES `projects` WRITE;
/*!40000 ALTER TABLE `projects` DISABLE KEYS */;
INSERT INTO `projects` VALUES (1,'Project1','2018-01-01',1,'2018-05-07 14:31:42','2018-05-07 14:31:42'),(2,'Project2','2018-02-02',2,'2018-05-07 14:32:02','2018-05-07 14:32:02'),(3,'Project3','2018-03-03',3,'2018-05-07 14:32:16','2018-05-07 14:32:16'),(4,'Project4','2018-04-04',4,'2018-05-07 14:32:31','2018-05-07 14:32:31'),(5,'Project5','2018-05-05',5,'2018-05-07 14:32:42','2018-05-07 14:32:42'),(6,'Project6','2018-06-06',6,'2018-05-07 14:32:57','2018-05-07 14:32:57'),(7,'Project7','2018-07-07',7,'2018-05-07 14:33:06','2018-05-07 14:33:06'),(8,'Project8','2018-08-08',8,'2018-05-07 14:33:18','2018-05-07 14:33:18'),(9,'Project9','2018-09-09',9,'2018-05-07 14:33:30','2018-05-07 14:33:30'),(10,'Project10','2018-10-10',10,'2018-05-07 14:33:42','2018-05-07 14:33:42'),(11,'Project11','2018-11-11',11,'2018-05-07 14:33:53','2018-05-07 14:33:53'),(12,'Project12','2018-12-12',12,'2018-05-07 14:34:07','2018-05-07 14:34:07');
/*!40000 ALTER TABLE `projects` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,NO_AUTO_VALUE_ON_ZERO,STRICT_TRANS_TABLES,STRICT_ALL_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER assign_least_pm AFTER INSERT ON projects
      FOR EACH ROW
      BEGIN
      SELECT u.id into @pm_id
      FROM users AS u
      WHERE u.type = 'ProjectManager'
            AND NOT EXISTS (SELECT *
                            FROM project_assignments AS pa
                            WHERE pa.project_manager_id = u.id)
            LIMIT 1;

      IF @pm_id IS NOT NULL THEN
        INSERT INTO project_assignments(project_id, project_manager_id, created_at, updated_at)
                    VALUES (NEW.id, @pm_id, NOW(), NOW());
      ELSE
        SELECT result.pm_id into @pm_id2
            FROM (SELECT project_manager_id AS pm_id, COUNT(*) AS ct
                  FROM project_assignments
                  GROUP BY pm_id
                  ORDER BY ct ASC
                  LIMIT 1) AS result;

        IF @pm_id2 IS NOT NULL THEN
          INSERT INTO project_assignments(project_id, project_manager_id, created_at, updated_at)
                      VALUES (NEW.id, @pm_id2, NOW(), NOW());
        END IF;

      END IF;
      END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `schema_migrations`
--

DROP TABLE IF EXISTS `schema_migrations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schema_migrations` (
  `version` varchar(255) NOT NULL,
  PRIMARY KEY (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schema_migrations`
--

LOCK TABLES `schema_migrations` WRITE;
/*!40000 ALTER TABLE `schema_migrations` DISABLE KEYS */;
INSERT INTO `schema_migrations` VALUES ('20180428134934'),('20180428135018'),('20180428135031'),('20180428135058'),('20180428135137'),('20180428135539'),('20180428141529'),('20180428182325'),('20180506184807');
/*!40000 ALTER TABLE `schema_migrations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_assignments`
--

DROP TABLE IF EXISTS `task_assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_assignments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_id` bigint(20) NOT NULL,
  `employee_id` bigint(20) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_task_assignments_on_employee_id` (`employee_id`),
  KEY `index_task_assignments_on_task_id` (`task_id`),
  CONSTRAINT `fk_rails_b7a2056e80` FOREIGN KEY (`task_id`) REFERENCES `tasks` (`id`),
  CONSTRAINT `fk_rails_cdd3e6b9ab` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_assignments`
--

LOCK TABLES `task_assignments` WRITE;
/*!40000 ALTER TABLE `task_assignments` DISABLE KEYS */;
INSERT INTO `task_assignments` VALUES (1,1,1,'2018-05-07 15:01:30','2018-05-07 15:01:30'),(2,2,2,'2018-05-07 15:03:57','2018-05-07 15:03:57'),(3,3,4,'2018-05-07 15:04:20','2018-05-07 15:04:20'),(4,3,6,'2018-05-07 15:04:23','2018-05-07 15:04:23'),(5,4,8,'2018-05-07 15:05:48','2018-05-07 15:05:48'),(6,5,2,'2018-05-07 15:06:40','2018-05-07 15:06:40'),(7,6,4,'2018-05-07 15:06:46','2018-05-07 15:06:46'),(8,6,6,'2018-05-07 15:06:49','2018-05-07 15:06:49'),(9,6,8,'2018-05-07 15:06:51','2018-05-07 15:06:51'),(10,7,1,'2018-05-07 15:08:44','2018-05-07 15:08:44'),(11,7,2,'2018-05-07 15:08:46','2018-05-07 15:08:46'),(12,8,3,'2018-05-07 15:08:54','2018-05-07 15:08:54'),(13,8,4,'2018-05-07 15:08:57','2018-05-07 15:08:57'),(14,10,5,'2018-05-07 15:09:12','2018-05-07 15:09:12'),(15,10,6,'2018-05-07 15:09:15','2018-05-07 15:09:15'),(16,11,7,'2018-05-07 15:09:21','2018-05-07 15:09:21'),(17,11,8,'2018-05-07 15:09:24','2018-05-07 15:09:24'),(18,12,3,'2018-05-07 15:33:36','2018-05-07 15:33:36'),(19,13,7,'2018-05-07 15:33:41','2018-05-07 15:33:41'),(20,13,8,'2018-05-07 15:33:44','2018-05-07 15:33:44'),(21,14,1,'2018-05-07 15:34:29','2018-05-07 15:34:29'),(22,14,2,'2018-05-07 15:34:30','2018-05-07 15:34:30'),(23,14,3,'2018-05-07 15:34:31','2018-05-07 15:34:31'),(24,15,6,'2018-05-07 15:34:37','2018-05-07 15:34:37'),(25,16,3,'2018-05-07 16:03:51','2018-05-07 16:03:51'),(26,17,2,'2018-05-07 16:03:54','2018-05-07 16:03:54'),(27,18,1,'2018-05-07 16:03:56','2018-05-07 16:03:56'),(28,20,4,'2018-05-07 16:06:16','2018-05-07 16:06:16'),(29,21,2,'2018-05-07 16:06:52','2018-05-07 16:06:52'),(30,21,8,'2018-05-07 16:06:55','2018-05-07 16:06:55'),(31,22,7,'2018-05-07 16:07:18','2018-05-07 16:07:18'),(32,22,8,'2018-05-07 16:07:25','2018-05-07 16:07:25'),(33,23,6,'2018-05-07 16:07:49','2018-05-07 16:07:49');
/*!40000 ALTER TABLE `task_assignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tasks`
--

DROP TABLE IF EXISTS `tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tasks` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `start_date` date NOT NULL,
  `total_days` int(11) NOT NULL DEFAULT '0',
  `project_id` bigint(20) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_tasks_on_project_id` (`project_id`),
  CONSTRAINT `fk_rails_02e851e3b7` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tasks`
--

LOCK TABLES `tasks` WRITE;
/*!40000 ALTER TABLE `tasks` DISABLE KEYS */;
INSERT INTO `tasks` VALUES (1,'Task221','2018-02-02',2,2,'2018-05-07 15:00:44','2018-05-07 15:00:44'),(2,'Task341','2018-04-04',2,4,'2018-05-07 15:03:08','2018-05-07 15:03:08'),(3,'Task342','2018-04-05',3,4,'2018-05-07 15:03:32','2018-05-07 15:03:32'),(4,'Task361','2018-06-06',6,6,'2018-05-07 15:05:42','2018-05-07 15:05:42'),(5,'Task381','2018-08-08',4,8,'2018-05-07 15:06:19','2018-05-07 15:06:19'),(6,'Task382','2018-08-12',4,8,'2018-05-07 15:06:36','2018-05-07 15:06:36'),(7,'Task3101','2018-10-10',2,10,'2018-05-07 15:07:42','2018-05-07 15:07:42'),(8,'Task3102','2018-10-12',2,10,'2018-05-07 15:07:56','2018-05-07 15:07:56'),(9,'Task3103','2018-10-14',2,10,'2018-05-07 15:08:11','2018-05-07 15:08:11'),(10,'Task3104','2018-10-16',2,10,'2018-05-07 15:08:21','2018-05-07 15:08:21'),(11,'Task3105','2018-10-18',2,10,'2018-05-07 15:08:36','2018-05-07 15:08:36'),(12,'Task151','2018-05-05',3,5,'2018-05-07 15:33:12','2018-05-07 15:33:12'),(13,'Task152','2018-05-05',5,5,'2018-05-07 15:33:26','2018-05-07 15:33:26'),(14,'Task171','2018-07-07',3,7,'2018-05-07 15:34:10','2018-05-07 15:34:10'),(15,'Task172','2018-07-10',4,7,'2018-05-07 15:34:24','2018-05-07 15:34:24'),(16,'Task191','2018-09-09',3,9,'2018-05-07 16:02:47','2018-05-07 16:02:47'),(17,'Task192','2018-09-09',6,9,'2018-05-07 16:03:01','2018-05-07 16:03:01'),(18,'Task193','2018-09-09',9,9,'2018-05-07 16:03:39','2018-05-07 16:03:39'),(19,'Task1111','2018-11-11',11,11,'2018-05-07 16:04:34','2018-05-07 16:04:34'),(20,'Task251','2018-05-05',4,5,'2018-05-07 16:06:12','2018-05-07 16:06:12'),(21,'Task271','2018-07-12',2,7,'2018-05-07 16:06:46','2018-05-07 16:06:46'),(22,'Task2111','2018-11-15',7,11,'2018-05-07 16:07:14','2018-05-07 16:07:14'),(23,'Task2112','2018-11-20',2,11,'2018-05-07 16:07:44','2018-05-07 16:07:44');
/*!40000 ALTER TABLE `tasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password_digest` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_users_on_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','$2a$10$lS/Aw4R1Le7oCY8owmYAju7XPC0P7LuOLL8ACG4TNG9sYvRTLowe6','Admin','One','Admin','2018-05-07 14:30:02','2018-05-07 14:30:02'),(2,'pm1','$2a$10$7WMMpZ.yasTN6Sq4p5oMCeazyOZOGpZmA6VK89rA36vXo9omhgUwa','Manager','One','ProjectManager','2018-05-07 14:35:05','2018-05-07 14:35:05'),(3,'pm2','$2a$10$3ywtSYmK8MLd2IfOfW7BeOijXO82x1KhqrOFyLdZt6q4i0TKFCnxG','Manager','Two','ProjectManager','2018-05-07 14:35:27','2018-05-07 14:35:27'),(4,'pm3','$2a$10$S1ZoVtNG9uqnNy9Vlbetmeft4rTTbOwqkBesH0wnad845Tl4CXsja','Manager','Three','ProjectManager','2018-05-07 14:35:45','2018-05-07 14:35:45'),(5,'pm4','$2a$10$wDUz2HL7W82r0A32tK3dU.pV9l2W0HSTcGNytmpFn5MEL.k0wdSFC','Manager','Four','ProjectManager','2018-05-07 14:36:02','2018-05-07 14:36:02'),(6,'admin2','$2a$10$aJdfzryIohRD2GIa7fntr.9OaBTWXyxE1bDHZDlB0HEUNeoHAgTGu','Admin','Two','Admin','2018-05-07 14:39:14','2018-05-07 14:39:14');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'proj_man_db'
--
/*!50003 DROP PROCEDURE IF EXISTS `complete_projects` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,NO_AUTO_VALUE_ON_ZERO,STRICT_TRANS_TABLES,STRICT_ALL_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `complete_projects`(IN pm_id CHAR(20))
BEGIN
        IF pm_id = 'ALL' THEN
          SELECT p.*
          FROM projects AS p
          WHERE EXISTS (SELECT *
                        FROM tasks
                        WHERE tasks.project_id = p.id)

                AND NOT EXISTS (SELECT *
                                FROM tasks AS t
                                WHERE t.project_id = p.id
                                      AND t.start_date + t.total_days > CURDATE());
        ELSE
          SELECT p.*
          FROM projects AS p, project_assignments AS pa
          WHERE (p.id = pa.project_id
                AND pa.project_manager_id = pm_id)
                AND (EXISTS (SELECT *
                            FROM tasks
                            WHERE tasks.project_id = p.id)
                AND NOT EXISTS (SELECT *
                                FROM tasks AS t
                                WHERE t.project_id = p.id
                                      AND t.start_date + t.total_days > CURDATE()));
        END IF;
      END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `incomplete_projects` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,NO_AUTO_VALUE_ON_ZERO,STRICT_TRANS_TABLES,STRICT_ALL_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `incomplete_projects`(IN pm_id CHAR(20))
BEGIN
        IF pm_id = 'ALL' THEN
          SELECT p.*
          FROM projects AS p
          WHERE NOT EXISTS (SELECT *
                            FROM tasks
                            WHERE tasks.project_id = p.id)

                OR EXISTS (SELECT *
                           FROM tasks AS t
                           WHERE t.project_id = p.id
                                 AND t.start_date + t.total_days > CURDATE());
        ELSE
          SELECT p.*
          FROM projects AS p, project_assignments AS pa
          WHERE (p.id = pa.project_id
                AND pa.project_manager_id = pm_id)
                AND (NOT EXISTS (SELECT *
                                FROM tasks
                                WHERE tasks.project_id = p.id)
                OR EXISTS (SELECT *
                           FROM tasks AS t
                           WHERE t.project_id = p.id
                                 AND t.start_date + t.total_days > CURDATE()));
        END IF;
      END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-05-07 23:38:06
