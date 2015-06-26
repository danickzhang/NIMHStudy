-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema als63f
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema als63f
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `als63f` DEFAULT CHARACTER SET latin1 ;
USE `als63f` ;

-- -----------------------------------------------------
-- Table `locationData`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `locationData` (
  `locationDataId` INT NOT NULL AUTO_INCREMENT,
  `latitude` DOUBLE NOT NULL,
  `longitude` DOUBLE NOT NULL,
  `accuracy` FLOAT NOT NULL,
  `provider` VARCHAR(45) NOT NULL,
  `type` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`locationDataId`),
  INDEX `idx_type` (`type` ASC))
ENGINE = InnoDB
COMMENT = 'Represents a report of the mobile devices locaton';


-- -----------------------------------------------------
-- Table `hardwareInfo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `hardwareInfo` (
  `hardwareInfoID` INT NOT NULL AUTO_INCREMENT,
  `message` TEXT NOT NULL COMMENT 'The message about the change in hardware settings.',
  PRIMARY KEY (`hardwareInfoID`))
ENGINE = InnoDB
COMMENT = 'Represents a message containing information about a hardware setting change on the users mobile device.';


-- -----------------------------------------------------
-- Table `login`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `login` (
  `username` VARCHAR(10) NOT NULL,
  `password` VARCHAR(40) NULL,
  PRIMARY KEY (`username`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
  `userID` VARCHAR(8) NOT NULL,
  `password` VARCHAR(40) NULL,
  `isAdmin` TINYINT(1) NULL DEFAULT 0,
  `date` VARCHAR(40) NULL,
  `study_day` INT(11) NULL DEFAULT -1,
  `study_week` INT(11) NULL DEFAULT -1,
  PRIMARY KEY (`userID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `randomSurvey`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `randomSurvey` (
  `UserID` VARCHAR(8) NOT NULL DEFAULT '',
  `Date` VARCHAR(30) NOT NULL DEFAULT '',
  `RSID` VARCHAR(5) NOT NULL DEFAULT '',
  `Completed` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`UserID`, `Date`, `RSID`),
  INDEX `fk_randomSurvey_users_idx` (`UserID` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `survey`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `survey` (
  `surveyID` VARCHAR(45) NOT NULL COMMENT 'The name of the XML file on the Android side in the assets/ directory. e.g. \"MorningReportParcel\"',
  `name` VARCHAR(45) NOT NULL COMMENT 'The display name for the survey i.e. Morning Survey	',
  PRIMARY KEY (`surveyID`))
ENGINE = InnoDB
COMMENT = 'Represents a survey of questions.';


-- -----------------------------------------------------
-- Table `question`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `question` (
  `questionID` VARCHAR(45) NOT NULL,
  `text` TEXT NOT NULL COMMENT 'The text of the question',
  PRIMARY KEY (`questionID`))
ENGINE = InnoDB
COMMENT = 'Represents a question, which may occur on more than one survey.';


-- -----------------------------------------------------
-- Table `questionOnSurvey`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `questionOnSurvey` (
  `surveyID` VARCHAR(45) NOT NULL COMMENT 'A link to the survey the question is on.',
  `questionID` VARCHAR(45) NOT NULL COMMENT 'A link to the actual question.\n',
  PRIMARY KEY (`surveyID`, `questionID`),
  INDEX `fk_QuestionOnSurvey_Survey1_idx` (`surveyID` ASC),
  INDEX `fk_QuestionOnSurvey_Question1_idx` (`questionID` ASC))
ENGINE = InnoDB
COMMENT = 'This table represents a particular question on a particular survey.';


-- -----------------------------------------------------
-- Table `surveySubmission`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `surveySubmission` (
  `surveySubmissionID` CHAR(36) NOT NULL,
  `surveyID` VARCHAR(45) NOT NULL COMMENT 'The survey that this is a submission (collection of answers) for.',
  PRIMARY KEY (`surveySubmissionID`),
  INDEX `fk_SurveySubmission_Survey1_idx` (`surveyID` ASC))
ENGINE = InnoDB
COMMENT = 'Represents a completed survey as comleted by a user.';


-- -----------------------------------------------------
-- Table `submissionAnswer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `submissionAnswer` (
  `submissionAnswerID` INT NOT NULL AUTO_INCREMENT,
  `surveySubmissionID` CHAR(36) NOT NULL COMMENT 'A link to a particular survey submission.',
  `questionID` VARCHAR(45) NOT NULL COMMENT 'A link to the question that this is an answer to.',
  `answer` INT NOT NULL COMMENT 'The answer to the question.',
  PRIMARY KEY (`submissionAnswerID`),
  INDEX `fk_SubmissionAnswer_SurveySubmission1_idx` (`surveySubmissionID` ASC),
  INDEX `fk_SubmissionAnswer_Question1_idx` (`questionID` ASC))
ENGINE = InnoDB
COMMENT = 'Represents a users answer for a particular question on a survey at a particular point int time.';


-- -----------------------------------------------------
-- Table `event`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `event` (
  `eventID` INT NOT NULL AUTO_INCREMENT,
  `userID` VARCHAR(8) NOT NULL COMMENT 'The user who\'s phone generated this event.',
  `timestamp` TIMESTAMP NOT NULL COMMENT 'The time the event record was created on the client.',
  `type` VARCHAR(45) NULL COMMENT 'The type of data reported by this event.  This value determines which foreign key to use to obtain the events information.',
  `studyDay` INT NOT NULL,
  `scheduledTS` TIMESTAMP NULL,
  `startTS` TIMESTAMP NULL,
  `endTS` TIMESTAMP NULL,
  `surveySubmissionID` CHAR(36) NULL COMMENT 'If the type of the event is a submission, this is the foreign key which links to the matching record in the SurveySubmission table.',
  `locationDataID` INT NULL COMMENT 'If the event a location report, this foreign key will point to the matching row in the LocationData table.',
  `hardwareInfoID` INT NULL COMMENT 'If the event type is a report of a hardware settings change, this foreign key will point to the matching entry in the HardwareInfo table.',
  `isSynchronized` TINYINT(1) NOT NULL,
  PRIMARY KEY (`eventID`),
  INDEX `fk_Event_HardwareInfo1_idx` (`hardwareInfoID` ASC),
  INDEX `fk_Event_LocationData1_idx` (`locationDataID` ASC),
  INDEX `fk_Event_users1_idx` (`userID` ASC),
  INDEX `fk_Event_SurveySubmission1_idx` (`surveySubmissionID` ASC),
  INDEX `fk_Event_userID_type` (`userID` ASC, `type` ASC),
  INDEX `index7` (`userID` ASC, `studyDay` ASC))
ENGINE = InnoDB
COMMENT = 'Represents an event which has occured on a mobile device.  The table holds the fields common to each event type and holds foreign keys to the tables holding the information for each type of event.';


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
