
CREATE TABLE IF NOT EXISTS `example`.`article` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` TEXT NOT NULL COMMENT 'Title',
  `create_date` DATETIME NOT NULL COMMENT 'Create Date',
  `update_date` DATETIME NOT NULL COMMENT 'Update Date',
  PRIMARY KEY (`id`))
ENGINE = InnoDB
COMMENT = 'Article';

CREATE TABLE IF NOT EXISTS `example`.`article_image` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `article_id` BIGINT UNSIGNED NOT NULL COMMENT 'Article ID',
  `small_url` VARCHAR(512) NOT NULL COMMENT 'URL',
  `large_url` VARCHAR(512) NOT NULL COMMENT 'URL',
  `create_date` DATETIME NOT NULL COMMENT 'Create Date',
  `update_date` DATETIME NOT NULL COMMENT 'Update Date',
  PRIMARY KEY (`id`),
  INDEX `ARTICLE_IMAGE-ARTICLE_ID` (`article_id` ASC),
  CONSTRAINT `ARTICLE_IMAGE-ARTICLE_ID`
    FOREIGN KEY (`article_id`)
    REFERENCES `example`.`article` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'Article Image';

ALTER TABLE `example`.`article_image` ADD COLUMN `thumb_url` VARCHAR(512) NOT NULL;
ALTER TABLE `example`.`article_image` CHANGE COLUMN `thumb_url` `thumb_url` TEXT NOT NULL;
ALTER TABLE `example`.`article_image` MODIFY COLUMN `thumb_url` VARCHAR(512) NOT NULL;

ALTER TABLE `example`.`article_image` ADD UNIQUE INDEX `ARTICLE_IMAGE-THUMB_URL` (`thumb_url` ASC);
ALTER TABLE `example`.`article_image` DROP INDEX `ARTICLE_IMAGE-THUMB_URL`;

ALTER TABLE `example`.`article_image` RENAME CONSTRAINT `ARTICLE_IMAGE-ARTICLE_ID` TO `ARTICLE_IMAGE-ARTICLE_ID-RENAMED`;
ALTER TABLE `example`.`article_image` DROP CONSTRAINT `ARTICLE_IMAGE-ARTICLE_ID-RENAMED`;

