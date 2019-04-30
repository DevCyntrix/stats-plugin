# SELECT @INNODB_BUF_SIZE := @@innodb_buffer_pool_size;
# SET GLOBAL innodb_buffer_pool_size=134217728;

SELECT @ID_NUM := IFNULL(MAX(id), 0)
FROM stats_arrows_shot;
INSERT INTO stats_arrows_shot (player, world, amount)
SELECT player, world, SUM(amount) as amount
FROM stats_arrows_shot
GROUP BY player, world;
DELETE
FROM stats_arrows_shot
WHERE id <= @ID_NUM;
ALTER TABLE `stats_arrows_shot`
  DROP COLUMN `timestamp`,
  ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER `amount`;

SELECT @rows := MAX(id)
FROM stats_beds_entered;
SELECT @rows := IF(@rows < 0, 0, @rows);
INSERT INTO stats_beds_entered (player, world, amount)
SELECT player, world, SUM(amount) as amount
FROM stats_beds_entered
GROUP BY player, world;
DELETE
FROM stats_beds_entered
WHERE id <= @rows;
ALTER TABLE `stats_beds_entered`
  DROP COLUMN `timestamp`,
  ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER `amount`;

SELECT @rows := MAX(id)
FROM stats_commands_performed;
SELECT @rows := IF(@rows < 0, 0, @rows);
INSERT INTO stats_commands_performed (player, world, amount)
SELECT player, world, SUM(amount) as amount
FROM stats_commands_performed
GROUP BY player, world;
DELETE
FROM stats_commands_performed
WHERE id <= @rows;
ALTER TABLE `stats_commands_performed`
  DROP COLUMN `timestamp`,
  ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER `amount`;

SELECT @rows := MAX(id)
FROM stats_eggs_thrown;
SELECT @rows := IF(@rows < 0, 0, @rows);
INSERT INTO stats_eggs_thrown (player, world, amount)
SELECT player, world, SUM(amount) as amount
FROM stats_eggs_thrown
GROUP BY player, world;
DELETE
FROM stats_eggs_thrown
WHERE id <= @rows;
ALTER TABLE `stats_eggs_thrown`
  DROP COLUMN `timestamp`,
  ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER `amount`;

SELECT @rows := MAX(id)
FROM stats_pvp_kill_streak;
SELECT @rows := IF(@rows < 0, 0, @rows);
INSERT INTO stats_pvp_kill_streak (player, world, amount)
SELECT player, world, SUM(amount) as amount
FROM stats_pvp_kill_streak
GROUP BY player, world;
DELETE
FROM stats_pvp_kill_streak
WHERE id <= @rows;
ALTER TABLE `stats_pvp_kill_streak`
  DROP COLUMN `timestamp`,
  ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER `amount`;

SELECT @rows := MAX(id)
FROM stats_pvp_kills;
SELECT @rows := IF(@rows < 0, 0, @rows);
INSERT INTO stats_pvp_kills (player, world, amount)
SELECT player, world, SUM(amount) as amount
FROM stats_pvp_kills
GROUP BY player, world;
DELETE
FROM stats_pvp_kills
WHERE id <= @rows;
ALTER TABLE `stats_pvp_kills`
  DROP COLUMN `timestamp`,
  ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER `amount`;

SELECT @rows := MAX(id)
FROM stats_teleports;
INSERT INTO stats_teleports (player, world, amount)
SELECT player, world, SUM(amount) as amount
FROM stats_teleports
GROUP BY player, world;
DELETE
FROM stats_teleports
WHERE id <= @rows;
ALTER TABLE `stats_teleports`
  DROP COLUMN `timestamp`,
  ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER `amount`;

SELECT @rows := MAX(id)
FROM stats_times_joined;
INSERT INTO stats_times_joined (player, world, amount)
SELECT player, world, SUM(amount) as amount
FROM stats_times_joined
GROUP BY player, world;
DELETE
FROM stats_times_joined
WHERE id <= @rows;
ALTER TABLE `stats_times_joined`
  DROP COLUMN `timestamp`,
  ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER `amount`;

SELECT @rows := MAX(id)
FROM stats_times_kicked;
INSERT INTO stats_times_kicked (player, world, amount)
SELECT player, world, SUM(amount) as amount
FROM stats_times_kicked
GROUP BY player, world;
DELETE
FROM stats_times_kicked
WHERE id <= @rows;
ALTER TABLE `stats_times_kicked`
  DROP COLUMN `timestamp`,
  ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER `amount`;

SELECT @rows := MAX(id)
FROM stats_words_said;
INSERT INTO stats_words_said (player, world, amount)
SELECT player, world, SUM(amount) as amount
FROM stats_words_said
GROUP BY player, world;
DELETE
FROM stats_words_said
WHERE id <= @rows;
ALTER TABLE `stats_words_said`
  DROP COLUMN `timestamp`,
  ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER `amount`;

SELECT @rows := MAX(id)
FROM stats_xp_gained;
INSERT INTO stats_xp_gained (player, world, amount)
SELECT player, world, SUM(amount) as amount
FROM stats_xp_gained
GROUP BY player, world;
DELETE
FROM stats_xp_gained
WHERE id <= @rows;
ALTER TABLE `stats_xp_gained`
  DROP COLUMN `timestamp`,
  ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER `amount`;

ALTER TABLE `stats_block_break`
  DROP COLUMN `loc_z`,
  DROP COLUMN `loc_y`,
  DROP COLUMN `loc_x`,
  DROP COLUMN `timestamp`,
  ADD COLUMN `amount` DOUBLE NOT NULL DEFAULT 1,
  ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER `amount`;
SELECT @rows := MAX(id)
FROM stats_block_break;
INSERT INTO stats_block_break (player, world, material, tool, amount)
SELECT player, world, material, tool, SUM(amount) as amount
FROM stats_block_break
GROUP BY player, world, material, tool;
DELETE
FROM stats_block_break
WHERE id <= @rows;

ALTER TABLE `stats_block_place`
  DROP COLUMN `loc_z`,
  DROP COLUMN `loc_y`,
  DROP COLUMN `loc_x`,
  DROP COLUMN `timestamp`,
  ADD COLUMN `amount` DOUBLE NOT NULL DEFAULT 1,
  ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER `amount`;
SELECT @rows := MAX(id)
FROM stats_block_place;
INSERT INTO stats_block_place (player, world, material, amount)
SELECT player, world, material, SUM(amount) as amount
FROM stats_block_place
GROUP BY player, world, material;
DELETE
FROM stats_block_place
WHERE id <= @rows;

SELECT @rows := MAX(id)
FROM stats_buckets_emptied;
INSERT INTO stats_buckets_emptied (player, world, amount, type)
SELECT player, world, SUM(amount) as amount, type
FROM stats_buckets_emptied
GROUP BY player, world, type;
DELETE
FROM stats_buckets_emptied
WHERE id <= @rows;
ALTER TABLE `stats_buckets_emptied`
    DROP COLUMN `id`,
    DROP COLUMN `timestamp`,
    ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER `amount`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL ,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`player`, `world`, `type`),
    DROP INDEX `id_UNIQUE`;

SELECT @rows := MAX(id)
FROM stats_damage_taken;
INSERT INTO stats_damage_taken (player, world, amount, type)
SELECT player, world, SUM(amount) as amount, type
FROM stats_damage_taken
GROUP BY player, world, type;
DELETE
FROM stats_damage_taken
WHERE id <= @rows;
ALTER TABLE `stats_damage_taken`
    DROP COLUMN `id`,
    DROP COLUMN `timestamp`,
    ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER `amount`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL ,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`type`, `world`, `player`),
    DROP INDEX `id_UNIQUE`;

ALTER TABLE `stats_death`
    DROP COLUMN `loc_z`,
    DROP COLUMN `loc_y`,
    DROP COLUMN `loc_x`,
    DROP COLUMN `timestamp`,
    ADD COLUMN `amount` DOUBLE NOT NULL DEFAULT 1,
    ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHANGE COLUMN `cause` `cause` VARCHAR(127) NOT NULL;
SELECT @rows := MAX(id)
FROM stats_death;
INSERT INTO stats_death (player, world, amount, cause)
SELECT player, world, SUM(amount) as amount, cause
FROM stats_death
GROUP BY player, world, cause;
DELETE
FROM stats_death
WHERE id <= @rows;

SELECT @rows := MAX(id)
FROM stats_fish_caught;
INSERT INTO stats_fish_caught (player, world, amount, type)
SELECT player, world, SUM(amount) as amount, type
FROM stats_fish_caught
GROUP BY player, world, type;
DELETE
FROM stats_fish_caught
WHERE id <= @rows;
ALTER TABLE `stats_fish_caught`
    DROP COLUMN `timestamp`,
    DROP COLUMN `id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`,
    ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`player`, `world`, `type`),
    DROP INDEX `id_UNIQUE`;

SELECT @rows := MAX(id)
FROM stats_food_consumed;
INSERT INTO stats_food_consumed (player, world, amount, type)
SELECT player, world, SUM(amount) as amount, type
FROM stats_food_consumed
GROUP BY player, world, type;
DELETE
FROM stats_food_consumed
WHERE id <= @rows;
ALTER TABLE `stats_food_consumed`
    DROP COLUMN `timestamp`,
    DROP COLUMN `id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`,
    ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`player`, `world`, `type`),
    DROP INDEX `id_UNIQUE`;

SELECT @rows := MAX(id)
FROM stats_items_crafted;
INSERT INTO stats_items_crafted (player, world, amount, type)
SELECT player, world, SUM(amount) as amount, type
FROM stats_items_crafted
GROUP BY player, world, type;
DELETE
FROM stats_items_crafted
WHERE id <= @rows;
ALTER TABLE `stats_items_crafted`
    DROP COLUMN `timestamp`,
    DROP COLUMN `id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`,
    ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`player`, `world`, `type`),
    DROP INDEX `id_UNIQUE`;

SELECT @rows := MAX(id)
FROM stats_items_dropped;
INSERT INTO stats_items_dropped (player, world, amount, type)
SELECT player, world, SUM(amount) as amount, type
FROM stats_items_dropped
GROUP BY player, world, type;
DELETE
FROM stats_items_dropped
WHERE id <= @rows;
ALTER TABLE `stats_items_dropped`
    DROP COLUMN `timestamp`,
    DROP COLUMN `id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`,
    ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`player`, `world`, `type`),
    DROP INDEX `id_UNIQUE`;

SELECT @rows := MAX(id)
FROM stats_items_picked_up;
INSERT INTO stats_items_picked_up (player, world, amount, type)
SELECT player, world, SUM(amount) as amount, type
FROM stats_items_picked_up
GROUP BY player, world, type;
DELETE
FROM stats_items_picked_up
WHERE id <= @rows;
ALTER TABLE `stats_items_picked_up`
    DROP COLUMN `timestamp`,
    DROP COLUMN `id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`,
    ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`player`, `world`, `type`),
    DROP INDEX `id_UNIQUE`;

ALTER TABLE `stats_kill`
  DROP COLUMN `timestamp`,
  ADD COLUMN `amount` DOUBLE NOT NULL DEFAULT 1 AFTER `weapon`,
  CHANGE COLUMN `victimType` `victimType` VARCHAR(127) NOT NULL ,
  CHANGE COLUMN `victimName` `victimName` VARCHAR(127) NOT NULL ,
  CHANGE COLUMN `weapon` `weapon` VARCHAR(127) NOT NULL ,
  ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
SELECT @rows := MAX(id)
FROM stats_kill;
INSERT INTO stats_kill (player, world, amount, victimType, victimName, weapon)
SELECT player, world, SUM(amount) as amount, victimType, victimName, weapon
FROM stats_kill
GROUP BY player, world, victimType, victimName, weapon;
DELETE
FROM stats_kill
WHERE id <= @rows;

SELECT @rows := MAX(id)
FROM stats_move;
INSERT INTO stats_move (player, world, amount, type)
SELECT player, world, SUM(amount) as amount, type
FROM stats_move
GROUP BY player, world, type;
DELETE
FROM stats_move
WHERE id <= @rows;
ALTER TABLE `stats_move`
    DROP COLUMN `timestamp`,
    DROP COLUMN `id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`,
    ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`player`, `world`, `type`),
    DROP INDEX `id_UNIQUE`;

CREATE TABLE IF NOT EXISTS `stats_players`
(
  `id`       int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uuid`     binary(16)       NOT NULL,
  `username` varchar(16)      NOT NULL DEFAULT 'Unknown',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid_UNIQUE` (`uuid`)
);

ALTER TABLE `stats_playtime`
  CHANGE COLUMN `amount` `amount` DOUBLE NOT NULL;

SELECT @rows := MAX(id)
FROM stats_times_sheared;
INSERT INTO stats_times_sheared (player, world, amount, type)
SELECT player, world, SUM(amount) as amount, type
FROM stats_times_sheared
GROUP BY player, world, type;
DELETE
FROM stats_times_sheared
WHERE id <= @rows;
ALTER TABLE `stats_times_sheared`
    DROP COLUMN `timestamp`,
    DROP COLUMN `id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`,
    ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`player`, `world`, `type`),
    DROP INDEX `id_UNIQUE`;

SELECT @rows := MAX(id)
FROM stats_tools_broken;
INSERT INTO stats_tools_broken (player, world, amount, type)
SELECT player, world, SUM(amount) as amount, type
FROM stats_tools_broken
GROUP BY player, world, type;
DELETE
FROM stats_tools_broken
WHERE id <= @rows;
ALTER TABLE `stats_tools_broken`
    DROP COLUMN `timestamp`,
    DROP COLUMN `id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`,
    ADD COLUMN `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`player`, `world`, `type`),
    DROP INDEX `id_UNIQUE`;

CREATE TABLE `stats_worlds`
(
  `id`      int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uuid`    binary(16)       NOT NULL,
  `name`    text             NOT NULL,
  `raining` tinyint(1)       NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid_UNIQUE` (`uuid`)
);

CREATE TABLE IF NOT EXISTS `stats_system`
(
  `version` int(10) unsigned NOT NULL
);

DELETE
FROM stats_system
WHERE version <= 2;
REPLACE INTO stats_system VALUE (2);

#
# SET GLOBAL innodb_buffer_pool_size=@INNODB_BUF_SIZE;
