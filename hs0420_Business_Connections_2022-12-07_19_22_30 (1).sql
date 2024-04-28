-- MySQL dump 10.19  Distrib 10.3.29-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 192.168.0.2    Database: hs0420_Business_Connections
-- ------------------------------------------------------
-- Server version	10.5.15-MariaDB-0+deb11u1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Company`
--

DROP TABLE IF EXISTS `Company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Company` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `companyName` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `companyName` (`companyName`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Company`
--

LOCK TABLES `Company` WRITE;
/*!40000 ALTER TABLE `Company` DISABLE KEYS */;
INSERT INTO `Company` VALUES (8,'Apple'),(7,'Disney'),(3,'Edvard Lifesciences'),(2,'Goldman Sachs'),(4,'Lucid'),(9,'McDonald\'s'),(1,'Scalar');
/*!40000 ALTER TABLE `Company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Connection`
--

DROP TABLE IF EXISTS `Connection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Connection` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `position` varchar(64) NOT NULL,
  `email` varchar(64) DEFAULT NULL,
  `phoneNumber` varchar(12) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Connection`
--

LOCK TABLES `Connection` WRITE;
/*!40000 ALTER TABLE `Connection` DISABLE KEYS */;
INSERT INTO `Connection` VALUES (1,'Hannah Saethereng','Valuation Analyst','hannah@hebb.no','8012274118'),(2,'Denise Dingsleder','Operations Analyst','denise@gmail.com','8013694556'),(3,'Lana Zbasnik','Data Analyst','lana@gmail.com','8015439873'),(8,'Bridger','Farmer','bridger@gmail.com',''),(11,'Maria','Influencer','maria@gmail.com',''),(12,'Julia Toiviainen','CEO','julia.toiviainen@gmail.com','8012459596'),(13,'Gustav Dalmalm','Banker','','');
/*!40000 ALTER TABLE `Connection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `InCompany`
--

DROP TABLE IF EXISTS `InCompany`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `InCompany` (
  `connection_id` int(11) NOT NULL,
  `company_id` int(11) NOT NULL,
  PRIMARY KEY (`connection_id`,`company_id`),
  KEY `company_id` (`company_id`),
  CONSTRAINT `InCompany_ibfk_1` FOREIGN KEY (`connection_id`) REFERENCES `Connection` (`id`) ON DELETE CASCADE,
  CONSTRAINT `InCompany_ibfk_2` FOREIGN KEY (`company_id`) REFERENCES `Company` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `InCompany`
--

LOCK TABLES `InCompany` WRITE;
/*!40000 ALTER TABLE `InCompany` DISABLE KEYS */;
INSERT INTO `InCompany` VALUES (1,1),(1,2),(2,2),(3,3),(8,1),(8,9),(12,2),(12,8);
/*!40000 ALTER TABLE `InCompany` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Note`
--

DROP TABLE IF EXISTS `Note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Note` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `text` varchar(3000) NOT NULL,
  `dateTaken` date NOT NULL,
  `connection_id` int(11) DEFAULT NULL,
  `company_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `connection_id` (`connection_id`),
  KEY `company_id` (`company_id`),
  CONSTRAINT `Note_ibfk_1` FOREIGN KEY (`connection_id`) REFERENCES `Connection` (`id`) ON DELETE CASCADE,
  CONSTRAINT `Note_ibfk_2` FOREIGN KEY (`company_id`) REFERENCES `Company` (`id`) ON DELETE CASCADE,
  CONSTRAINT `CONSTRAINT_1` CHECK (`connection_id` is not null or `company_id` is not null)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Note`
--

LOCK TABLES `Note` WRITE;
/*!40000 ALTER TABLE `Note` DISABLE KEYS */;
INSERT INTO `Note` VALUES (1,'Hannah graduates College in 2024','2022-11-29',1,NULL),(2,'Denise is very nice and bright. She will soon graduate with a degree in Finance and Computer Information Systems','2022-11-29',2,NULL),(3,'Lana is a very good skier who skis for the westminster Ski Team.','2022-11-29',3,NULL),(4,'Scalar is a valuation firm located in Draper. They ususally highers 2-4 summer interns and 1-2 winter interns every year.','2022-11-30',NULL,1),(6,'Bridger is very good at hunting','2022-12-01',8,NULL),(8,'Hannah is sick at coding','2022-12-01',1,NULL),(9,'Disney makes great movies','2022-12-07',NULL,7),(10,'Julia is a very talented CEO. An she has awesome personality as well.','2022-12-07',12,NULL),(11,'They make great burgers','2022-12-07',NULL,9);
/*!40000 ALTER TABLE `Note` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-12-08  2:22:30
