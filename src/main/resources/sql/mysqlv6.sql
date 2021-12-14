# noinspection SqlWithoutWhereForFile

INSERT IGNORE INTO stats_players (uuid)
SELECT DISTINCT(player)
FROM stats_playtime;
INSERT IGNORE INTO stats_worlds (uuid)
SELECT DISTINCT(world)
FROM stats_playtime;

ALTER TABLE `stats_arrows_shot`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_arrows_shot` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_arrows_shot` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_arrows_shot`
    DROP INDEX `unique_idx`,
    DROP INDEX `uuid_world`,
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC),
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_arrows_shot`
    ADD CONSTRAINT `stats_arrows_shot_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_arrows_shot_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_arrows_shot`
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_beds_entered`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_beds_entered` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;

UPDATE `stats_beds_entered` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_beds_entered`
    DROP INDEX `unique_idx`,
    DROP INDEX `uuid_world`,
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC),
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_beds_entered`
    ADD CONSTRAINT `stats_beds_entered_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_beds_entered_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_beds_entered`
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_block_break`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_block_break` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_block_break` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_block_break`
    DROP INDEX `rest_unique`,
    DROP INDEX `uuid_world`,
    DROP INDEX `id_UNIQUE`,
    DROP PRIMARY KEY,
    DROP COLUMN `id`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_block_break`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC, `material` ASC, `tool` ASC),
    ADD CONSTRAINT `stats_block_break_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_block_break_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_block_break`
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_block_place`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_block_place` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_block_place` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_block_place`
    DROP INDEX `rest_unique`,
    DROP KEY `uuid`,
    DROP INDEX `id_UNIQUE`,
    DROP PRIMARY KEY,
    DROP COLUMN `id`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_block_place`
    ADD CONSTRAINT `stats_block_place_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_block_place_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_block_place`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC, `material` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_buckets_emptied`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`;

UPDATE `stats_buckets_emptied` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_buckets_emptied` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_buckets_emptied`
    DROP PRIMARY KEY,
    DROP INDEX `uuid_world`,
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC, `type` ASC),
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_buckets_emptied`
    ADD CONSTRAINT `stats_buckets_emptied_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_buckets_emptied_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_buckets_emptied`
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_commands_performed`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_commands_performed` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_commands_performed` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_commands_performed`
    DROP INDEX `unique_idx`,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_commands_performed`
    ADD CONSTRAINT `stats_commands_performed_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_commands_performed_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_commands_performed`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_damage_taken`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`;

UPDATE `stats_damage_taken` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_damage_taken` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_damage_taken`
    DROP PRIMARY KEY ,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_damage_taken`
    ADD CONSTRAINT `stats_damage_taken_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_damage_taken_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_damage_taken`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC, `type` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_death`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_death` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_death` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_death`
    DROP INDEX `unique_idx`,
    DROP INDEX `uuid`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_death`
    ADD CONSTRAINT `stats_death_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_death_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_death`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC, `cause` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_eggs_thrown`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_eggs_thrown` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_eggs_thrown` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_eggs_thrown`
    DROP INDEX `unique_idx`,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_eggs_thrown`
    ADD CONSTRAINT `stats_eggs_thrown_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_eggs_thrown_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_eggs_thrown`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_fish_caught`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`;

UPDATE `stats_fish_caught` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_fish_caught` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_fish_caught`
    DROP PRIMARY KEY ,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_fish_caught`
    ADD CONSTRAINT `stats_fish_caught_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_fish_caught_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_fish_caught`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC, `type` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_food_consumed`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`;

UPDATE `stats_food_consumed` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_food_consumed` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_food_consumed`
    DROP PRIMARY KEY ,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_food_consumed`
    ADD CONSTRAINT `stats_food_consumed_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_food_consumed_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_food_consumed`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC, `type` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_items_crafted`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`;

UPDATE `stats_items_crafted` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_items_crafted` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_items_crafted`
    DROP PRIMARY KEY ,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_items_crafted`
    ADD CONSTRAINT `stats_items_crafted_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_items_crafted_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_items_crafted`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC, `type` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_items_dropped`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`;

UPDATE `stats_items_dropped` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_items_dropped` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_items_dropped`
    DROP PRIMARY KEY ,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_items_dropped`
    ADD CONSTRAINT `stats_items_dropped_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_items_dropped_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_items_dropped`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC, `type` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_items_picked_up`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`;

UPDATE `stats_items_picked_up` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_items_picked_up` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_items_picked_up`
    DROP PRIMARY KEY ,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_items_picked_up`
    ADD CONSTRAINT `stats_items_picked_up_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_items_picked_up_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_items_picked_up`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC, `type` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_kill`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_kill` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_kill` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_kill`
    DROP INDEX `unique_idx`,
    DROP INDEX `uuid`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_kill`
    ADD CONSTRAINT `stats_kill_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_kill_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_kill`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC, `victimType` ASC, `victimName` ASC, `weapon` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_last_join`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_last_join` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_last_join` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_last_join`
    DROP INDEX `uuid`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_last_join`
    ADD CONSTRAINT `stats_last_join_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_last_join_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_last_join`
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_last_quit`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_last_quit` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_last_quit` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_last_quit`
    DROP INDEX `uuid`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_last_quit`
    ADD CONSTRAINT `stats_last_quit_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_last_quit_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_last_quit`
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_move`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`;

UPDATE `stats_move` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_move` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_move`
    DROP PRIMARY KEY ,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_move`
    ADD CONSTRAINT `stats_move_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_move_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_move`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC, `type` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_playtime`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

set @exist := (select count(*)
               from information_schema.statistics
               where table_name = 'stats_playtime'
                 and index_name = 'unique_idx'
                 and table_schema = database());
set @sqlstmt :=
        if(@exist = 0, 'select ''INFO: Index doesnt exist.''', 'ALTER TABLE stats_playtime DROP INDEX unique_idx');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
UPDATE `stats_playtime` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_playtime` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_playtime`
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_playtime`
    ADD CONSTRAINT `stats_playtime_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_playtime_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_playtime`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_pvp_kill_streak`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_pvp_kill_streak` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_pvp_kill_streak` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_pvp_kill_streak`
    DROP INDEX `unique_idx`,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_pvp_kill_streak`
    ADD CONSTRAINT `stats_pvp_kill_streak_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_pvp_kill_streak_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_pvp_kill_streak`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_pvp_kills`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_pvp_kills` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_pvp_kills` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_pvp_kills`
    DROP INDEX `unique_idx`,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_pvp_kills`
    ADD CONSTRAINT `stats_pvp_kills_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_pvp_kills_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_pvp_kills`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_teleports`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_teleports` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_teleports` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_teleports`
    DROP INDEX `unique_idx`,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_teleports`
    ADD CONSTRAINT `stats_teleports_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_teleports_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_teleports`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_times_joined`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_times_joined` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_times_joined` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_times_joined`
    DROP INDEX `unique_idx`,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_times_joined`
    ADD CONSTRAINT `stats_times_joined_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_times_joined_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_times_joined`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_times_kicked`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_times_kicked` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_times_kicked` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_times_kicked`
    DROP INDEX `unique_idx`,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_times_kicked`
    ADD CONSTRAINT `stats_times_kicked_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_times_kicked_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_times_kicked`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_times_sheared`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`;

UPDATE `stats_times_sheared` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_times_sheared` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_times_sheared`
    DROP PRIMARY KEY,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_times_sheared`
    ADD CONSTRAINT `stats_times_sheared_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_times_sheared_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_times_sheared`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC, `type` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_tools_broken`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`,
    CHANGE COLUMN `type` `type` VARCHAR(127) NOT NULL AFTER `world`;

UPDATE `stats_tools_broken` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_tools_broken` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_tools_broken`
    DROP PRIMARY KEY ,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_tools_broken`
    ADD CONSTRAINT `stats_tools_broken_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_tools_broken_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_tools_broken`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC, `type` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_trades_performed`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_trades_performed` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_trades_performed` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_trades_performed`
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_trades_performed`
    ADD CONSTRAINT `stats_trades_performed_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_trades_performed_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_trades_performed`
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_words_said`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_words_said` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_words_said` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_words_said`
    DROP INDEX `unique_idx`,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_words_said`
    ADD CONSTRAINT `stats_words_said_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_words_said_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_words_said`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


ALTER TABLE `stats_xp_gained`
    ADD COLUMN `player_id` INT UNSIGNED NOT NULL FIRST,
    ADD COLUMN `world_id` INT UNSIGNED NOT NULL AFTER `player_id`;

UPDATE `stats_xp_gained` a INNER JOIN stats_players b ON a.player = b.uuid
SET a.player_id=b.id;
UPDATE `stats_xp_gained` a INNER JOIN stats_worlds b ON a.world = b.uuid
SET a.world_id=b.id;

ALTER TABLE `stats_xp_gained`
    DROP INDEX `unique_idx`,
    DROP INDEX `uuid_world`,
    ADD INDEX `p_id_idx` (`player_id` ASC),
    ADD INDEX `w_id_idx` (`world_id` ASC);

ALTER TABLE `stats_xp_gained`
    ADD CONSTRAINT `stats_xp_gained_p_id`
        FOREIGN KEY (`player_id`)
            REFERENCES `stats_players` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    ADD CONSTRAINT `stats_xp_gained_w_id`
        FOREIGN KEY (`world_id`)
            REFERENCES `stats_worlds` (`id`)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

ALTER TABLE `stats_xp_gained`
    ADD PRIMARY KEY (`player_id` ASC, `world_id` ASC),
    DROP COLUMN `world`,
    DROP COLUMN `player`;


DELETE
FROM stats_system
WHERE version <= 6;

REPLACE INTO stats_system VALUE (6);
