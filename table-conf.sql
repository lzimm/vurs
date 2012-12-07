


CREATE TABLE `concept` (
`id` INT(11) NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

ALTER TABLE `concept` ADD COLUMN `path` VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE `concept` ADD INDEX `index_path` (`path`);
ALTER TABLE `concept` ADD UNIQUE KEY `unique_path` (`path`);
ALTER TABLE `concept` ADD COLUMN `name` VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE `concept` ADD INDEX `index_name` (`name`);
ALTER TABLE `concept` ADD COLUMN `parent` INT(11) NOT NULL DEFAULT 0;
ALTER TABLE `concept` ADD INDEX `index_parent` (`parent`);
ALTER TABLE `concept` ADD COLUMN `source` INT(11) NOT NULL DEFAULT 0;
ALTER TABLE `concept` ADD COLUMN `state` INT(11) NOT NULL DEFAULT 0;
ALTER TABLE `concept` ADD COLUMN `avatar` VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE `concept` ADD COLUMN `description` TEXT NOT NULL DEFAULT '';



CREATE TABLE `conceptlink` (
`id` INT(11) NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

ALTER TABLE `conceptlink` ADD COLUMN `concept` INT(11) NOT NULL DEFAULT 0;
ALTER TABLE `conceptlink` ADD INDEX `index_concept` (`concept`);
ALTER TABLE `conceptlink` ADD COLUMN `state` INT(11) NOT NULL DEFAULT 0;
ALTER TABLE `conceptlink` ADD INDEX `index_state` (`state`);
ALTER TABLE `conceptlink` ADD COLUMN `stateTime` TIMESTAMP NOT NULL DEFAULT '1970-01-01 00:00:01';
ALTER TABLE `conceptlink` ADD INDEX `index_stateTime` (`stateTime`);
ALTER TABLE `conceptlink` ADD COLUMN `stateOwner` VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE `conceptlink` ADD INDEX `index_stateOwner` (`stateOwner`);
ALTER TABLE `conceptlink` ADD COLUMN `link` VARCHAR(512) NOT NULL DEFAULT '';



CREATE TABLE `activitybadgecriteria` (
`id` INT(11) NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

ALTER TABLE `activitybadgecriteria` ADD COLUMN `badge` INT(11) NOT NULL DEFAULT 0;
ALTER TABLE `activitybadgecriteria` ADD INDEX `index_badge` (`badge`);
ALTER TABLE `activitybadgecriteria` ADD COLUMN `equivalenceOrder` INT(11) NOT NULL DEFAULT 0;
ALTER TABLE `activitybadgecriteria` ADD COLUMN `activity` INT(11) NOT NULL DEFAULT 0;
ALTER TABLE `activitybadgecriteria` ADD INDEX `index_activity` (`activity`);
ALTER TABLE `activitybadgecriteria` ADD COLUMN `pingCount` INT(11) NOT NULL DEFAULT 0;



CREATE TABLE `badgedefinition` (
`id` INT(11) NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

ALTER TABLE `badgedefinition` ADD COLUMN `parent` INT(11) NOT NULL DEFAULT 0;
ALTER TABLE `badgedefinition` ADD INDEX `index_parent` (`parent`);
ALTER TABLE `badgedefinition` ADD COLUMN `name` VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE `badgedefinition` ADD COLUMN `description` VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE `badgedefinition` ADD COLUMN `asset` VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE `badgedefinition` ADD COLUMN `type` INT(11) NOT NULL DEFAULT 0;



CREATE TABLE `user` (
`id` INT(11) NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

ALTER TABLE `user` ADD COLUMN `name` VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE `user` ADD INDEX `index_name` (`name`);
ALTER TABLE `user` ADD UNIQUE KEY `unique_name` (`name`);
ALTER TABLE `user` ADD COLUMN `password` VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE `user` ADD COLUMN `email` VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE `user` ADD COLUMN `isAdmin` TINYINT(1) NOT NULL DEFAULT 0;



CREATE TABLE `activitybadgeindex` (
`id` INT(11) NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

ALTER TABLE `activitybadgeindex` ADD COLUMN `activity` INT(11) NOT NULL DEFAULT 0;
ALTER TABLE `activitybadgeindex` ADD INDEX `index_activity` (`activity`);
ALTER TABLE `activitybadgeindex` ADD COLUMN `badge` INT(11) NOT NULL DEFAULT 0;
ALTER TABLE `activitybadgeindex` ADD INDEX `index_badge` (`badge`);



CREATE TABLE `activitydefinition` (
`id` INT(11) NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

ALTER TABLE `activitydefinition` ADD COLUMN `name` VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE `activitydefinition` ADD COLUMN `concept` INT(11) NOT NULL DEFAULT 0;



CREATE TABLE `neighbourhoodpingjob` (
`id` INT(11) NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

ALTER TABLE `neighbourhoodpingjob` ADD COLUMN `ping` INT(11) NOT NULL DEFAULT 0;
ALTER TABLE `neighbourhoodpingjob` ADD INDEX `index_ping` (`ping`);
ALTER TABLE `neighbourhoodpingjob` ADD COLUMN `state` INT(11) NOT NULL DEFAULT 0;
ALTER TABLE `neighbourhoodpingjob` ADD INDEX `index_state` (`state`);
ALTER TABLE `neighbourhoodpingjob` ADD COLUMN `stateTime` TIMESTAMP NOT NULL DEFAULT '1970-01-01 00:00:01';
ALTER TABLE `neighbourhoodpingjob` ADD INDEX `index_stateTime` (`stateTime`);
ALTER TABLE `neighbourhoodpingjob` ADD COLUMN `stateOwner` VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE `neighbourhoodpingjob` ADD INDEX `index_stateOwner` (`stateOwner`);
