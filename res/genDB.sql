-- --------------------------------------------------------
-- Host:                         147.172.177.230
-- Server Version:               10.1.34-MariaDB-0ubuntu0.18.04.1 - Ubuntu 18.04
-- Server Betriebssystem:        debian-linux-gnu
-- HeidiSQL Version:             9.5.0.5196
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Exportiere Datenbank Struktur für test_robot
CREATE DATABASE IF NOT EXISTS `test_robot` /*!40100 DEFAULT CHARACTER SET ascii COLLATE ascii_bin */;
USE `test_robot`;

-- Daten Export vom Benutzer nicht ausgewählt
-- Exportiere Struktur von Tabelle test_robot.tbl_functions
CREATE TABLE IF NOT EXISTS `tbl_functions` (
  `function_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `function_path` varchar(150) COLLATE ascii_bin NOT NULL,
  `function_name` varchar(100) COLLATE ascii_bin NOT NULL,
  PRIMARY KEY (`function_id`),
  UNIQUE KEY `SECONDARY` (`function_path`,`function_name`)
) ENGINE=InnoDB DEFAULT CHARSET=ascii COLLATE=ascii_bin;

-- Exportiere Struktur von Tabelle test_robot.tbl_bugs
CREATE TABLE IF NOT EXISTS `tbl_bugs` (
  `bug_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `function_id` int(10) unsigned NOT NULL,
  `bug_date` datetime DEFAULT NULL,
  `bug_repository` mediumtext COLLATE ascii_bin,
  `bug_commit` varchar(40) COLLATE ascii_bin DEFAULT NULL,
  `bug_severity` varchar(10) COLLATE ascii_bin NOT NULL,
  `bug_line` int(10) unsigned NOT NULL,
  `bug_source` char(14) COLLATE ascii_bin NOT NULL,
  PRIMARY KEY (`bug_id`),
  KEY `fk_tbl_bugs_tbl_functions_idx` (`function_id`),
  CONSTRAINT `fk_tbl_bugs_tbl_functions` FOREIGN KEY (`function_id`) REFERENCES `tbl_functions` (`function_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=ascii COLLATE=ascii_bin;

-- Daten Export vom Benutzer nicht ausgewählt
-- Exportiere Struktur von Tabelle test_robot.tbl_linux
CREATE TABLE IF NOT EXISTS `tbl_linux` (
  `linux_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` varchar(10) COLLATE ascii_bin NOT NULL,
  PRIMARY KEY (`linux_id`),
  UNIQUE KEY `Schluessel 2` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=ascii COLLATE=ascii_bin;

-- Daten Export vom Benutzer nicht ausgewählt
-- Exportiere Struktur von Tabelle test_robot.tbl_linux_measure
CREATE TABLE IF NOT EXISTS `tbl_linux_measure` (
  `measure_id` int(10) NOT NULL AUTO_INCREMENT,
  `linux_id` int(10) unsigned NOT NULL,
  `function_id` int(10) unsigned NOT NULL,
  `linux_measure_line` int(10) unsigned NOT NULL,
  PRIMARY KEY (`measure_id`),
  UNIQUE KEY `Schluessel 5` (`measure_id`,`function_id`),
  KEY `fk_table1_tbl_linux1_idx` (`linux_id`),
  KEY `fk_table1_tbl_functions1_idx` (`function_id`),
  CONSTRAINT `fk_table1_tbl_functions1` FOREIGN KEY (`function_id`) REFERENCES `tbl_functions` (`function_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_table1_tbl_linux1` FOREIGN KEY (`linux_id`) REFERENCES `tbl_linux` (`linux_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=ascii COLLATE=ascii_bin;

-- Daten Export vom Benutzer nicht ausgewählt
-- Exportiere Struktur von Tabelle test_robot.tbl_linux_measure_values
CREATE TABLE IF NOT EXISTS `tbl_linux_measure_values` (
  `measure_id` int(10) NOT NULL,
  `metric_id` smallint(5) unsigned NOT NULL,
  `value` decimal(19,2) NOT NULL,
  UNIQUE KEY `Schluesel 3` (`measure_id`,`metric_id`),
  KEY `FK_metric` (`metric_id`)
) ENGINE=InnoDB DEFAULT CHARSET=ascii COLLATE=ascii_bin
/*!50100 PARTITION BY HASH (metric_id) */;

-- Daten Export vom Benutzer nicht ausgewählt
-- Exportiere Struktur von Tabelle test_robot.tbl_metric
CREATE TABLE IF NOT EXISTS `tbl_metric` (
  `metric_id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `metric_name` varchar(160) COLLATE ascii_bin NOT NULL,
  PRIMARY KEY (`metric_id`),
  UNIQUE KEY `metric_name_UNIQUE` (`metric_name`)
) ENGINE=InnoDB DEFAULT CHARSET=ascii COLLATE=ascii_bin;

-- Daten Export vom Benutzer nicht ausgewählt
-- Exportiere Struktur von Tabelle test_robot.tbl_metric_descriptions
CREATE TABLE IF NOT EXISTS `tbl_metric_descriptions` (
  `metric_descriptions_id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `metric_descriptions_category` varchar(50) COLLATE ascii_bin DEFAULT NULL,
  `metric_descriptions_min_metric` smallint(5) unsigned NOT NULL,
  `metric_descriptions_max_metric` smallint(5) unsigned NOT NULL,
  `metric_descriptions_description` varchar(250) COLLATE ascii_bin DEFAULT NULL,
  KEY `Schlüssel 1` (`metric_descriptions_id`)
) ENGINE=InnoDB DEFAULT CHARSET=ascii COLLATE=ascii_bin;

-- Daten Export vom Benutzer nicht ausgewählt
-- Exportiere Struktur von Tabelle test_robot.tbl_error_measures
CREATE TABLE IF NOT EXISTS `tbl_error_measures` (
  `metric_id` smallint(5) unsigned NOT NULL,
  `bug_id` int(10) unsigned NOT NULL,
  `value` decimal(19,2) NOT NULL,
  PRIMARY KEY (`metric_id`,`bug_id`,`value`),
  KEY `fk_tbl_error_measures_tbl_metric1_idx` (`metric_id`),
  KEY `fk_tbl_error_measures_tbl_bugs1_idx` (`bug_id`),
  CONSTRAINT `ref_bug` FOREIGN KEY (`bug_id`) REFERENCES `tbl_bugs` (`bug_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `ref_metric` FOREIGN KEY (`metric_id`) REFERENCES `tbl_metric` (`metric_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=ascii COLLATE=ascii_bin;

-- Daten Export vom Benutzer nicht ausgewählt
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
