-- MySQL dump 10.13  Distrib 8.0.12
--
-- Host: localhost    Database: -
-- ------------------------------------------------------
-- Server version	8.0.13

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
SET NAMES utf8;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

--
-- Table structure for table `stats_arrows_shot`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_arrows_shot`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_beds_entered`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_beds_entered`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_block_break`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_block_break`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `loc_x`     int(11)             NOT NULL,
  `loc_y`     int(11)             NOT NULL,
  `loc_z`     int(11)             NOT NULL,
  `material`  text                NOT NULL,
  `tool`      text                NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_block_place`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_block_place`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `loc_x`     int(11)             NOT NULL,
  `loc_y`     int(11)             NOT NULL,
  `loc_z`     int(11)             NOT NULL,
  `material`  text                NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_buckets_emptied`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_buckets_emptied`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `type`      text                NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_commands_performed`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_commands_performed`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_damage_taken`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_damage_taken`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `type`      text                NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_death`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_death`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `loc_x`     int(11)             NOT NULL,
  `loc_y`     int(11)             NOT NULL,
  `loc_z`     int(11)             NOT NULL,
  `cause`     text                NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_eggs_thrown`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_eggs_thrown`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_fish_caught`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_fish_caught`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `type`      text                NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_food_consumed`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_food_consumed`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `type`      text                NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_items_crafted`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_items_crafted`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `type`      text                NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_items_dropped`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_items_dropped`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `type`      text                NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_items_picked_up`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_items_picked_up`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `type`      text                NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_kill`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_kill`
(
  `id`         bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`     binary(16)          NOT NULL,
  `world`      binary(16)          NOT NULL,
  `victimType` text                NOT NULL,
  `victimName` text                NOT NULL,
  `weapon`     text                NOT NULL,
  `timestamp`  timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_last_join`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_last_join`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_last_quit`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_last_quit`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_move`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_move`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `type`      text                NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_playtime`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_playtime`
(
  `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`       binary(16)          NOT NULL,
  `world`        binary(16)          NOT NULL,
  `amount`       bigint(20)          NOT NULL,
  `last_updated` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `uuid_world` (`player`, `world`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_pvp_kill_streak`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_pvp_kill_streak`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_pvp_kills`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_pvp_kills`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_teleports`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_teleports`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_times_joined`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_times_joined`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_times_kicked`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_times_kicked`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_times_sheared`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_times_sheared`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `type`      text                NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_tools_broken`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_tools_broken`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `type`      text                NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_trades_performed`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_trades_performed`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `item`      text                NOT NULL,
  `price`     text                NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_words_said`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_words_said`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `stats_xp_gained`
--

/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE IF NOT EXISTS `stats_xp_gained`
(
  `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `player`    binary(16)          NOT NULL,
  `world`     binary(16)          NOT NULL,
  `amount`    double              NOT NULL,
  `timestamp` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `uuid_world` (`player`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

-- Dump completed on 2019-01-29 22:29:21
