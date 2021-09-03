# Host: localhost  (Version: 5.5.40)
# Date: 2016-06-06 12:04:28
# Generator: MySQL-Front 5.3  (Build 4.120)

/*!40101 SET NAMES utf8 */;

#
# Structure for table "canvasline"
#

DROP TABLE IF EXISTS `canvasline`;
CREATE TABLE `canvasline` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `canvas_id` int(11) DEFAULT '0',
  `line_width` varchar(255) NOT NULL DEFAULT '',
  `line_style` varchar(255) NOT NULL DEFAULT '',
  `line_sites` text NOT NULL,
  `time` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Id`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

#
# Data for table "canvasline"
#

/*!40000 ALTER TABLE `canvasline` DISABLE KEYS */;
/*!40000 ALTER TABLE `canvasline` ENABLE KEYS */;
