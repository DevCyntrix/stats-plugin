set @exist := (select count(*)
               from information_schema.statistics
               where table_name = 'stats_block_break'
                 and index_name = 'rest_unique'
                 and table_schema = database());
set @sqlstmt :=
        if(@exist = 0, 'select ''INFO: Index doesnt exist.''', 'ALTER TABLE stats_block_break DROP INDEX rest_unique');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;

set @exist := (select count(*)
               from information_schema.statistics
               where table_name = 'stats_block_place'
                 and index_name = 'rest_unique'
                 and table_schema = database());
set @sqlstmt :=
        if(@exist = 0, 'select ''INFO: Index doesnt exist.''', 'ALTER TABLE stats_block_place DROP INDEX rest_unique');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;

SELECT @rows := MAX(id)
FROM stats_block_break;
INSERT INTO stats_block_break (player, world, material, tool, amount)
SELECT player, world, material, tool, SUM(amount) as amount
FROM stats_block_break
GROUP BY player, world, material, tool;
DELETE
FROM stats_block_break
WHERE id <= @rows;

SELECT @rows := MAX(id)
FROM stats_block_place;
INSERT INTO stats_block_place (player, world, material, amount)
SELECT player, world, material, SUM(amount) as amount
FROM stats_block_place
GROUP BY player, world, material;
DELETE
FROM stats_block_place
WHERE id <= @rows;

ALTER TABLE stats_block_break
    CHANGE COLUMN `material` `material` VARCHAR(90) CHARACTER SET 'latin1' NOT NULL ,
    CHANGE COLUMN `tool` `tool` VARCHAR(90) CHARACTER SET 'latin1' NOT NULL ,
    ADD UNIQUE INDEX `rest_unique` (`player` ASC, `world` ASC, `material` ASC, `tool` ASC);

ALTER TABLE stats_block_place
    CHANGE COLUMN `material` `material` VARCHAR(127) CHARACTER SET 'latin1' NOT NULL ,
    ADD UNIQUE INDEX `rest_unique` (`player` ASC, `world` ASC, `material` ASC);

DELETE
FROM stats_system
WHERE version <= 5;
REPLACE INTO stats_system VALUE (5);
