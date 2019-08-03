ALTER TABLE `stats_arrows_shot`
  DROP COLUMN `id`,
  DROP INDEX `id_UNIQUE` ,
  DROP PRIMARY KEY,
  ADD UNIQUE INDEX `unique_idx` (`player` ASC, `world` ASC);
ALTER TABLE `stats_beds_entered`
  DROP COLUMN `id`,
  DROP INDEX `id_UNIQUE` ,
  DROP PRIMARY KEY,
  ADD UNIQUE INDEX `unique_idx` (`player` ASC, `world` ASC);
ALTER TABLE `stats_commands_performed`
  DROP COLUMN `id`,
  DROP INDEX `id_UNIQUE` ,
  DROP PRIMARY KEY,
  ADD UNIQUE INDEX `unique_idx` (`player` ASC, `world` ASC);
ALTER TABLE `stats_death`
  DROP COLUMN `id`,
  DROP INDEX `id_UNIQUE` ,
  DROP PRIMARY KEY,
  ADD UNIQUE INDEX `unique_idx` (`player` ASC, `world` ASC, `cause` ASC);
ALTER TABLE `stats_eggs_thrown`
  DROP COLUMN `id`,
  DROP INDEX `id_UNIQUE` ,
  DROP PRIMARY KEY,
  ADD UNIQUE INDEX `unique_idx` (`player` ASC, `world` ASC);
ALTER TABLE `stats_kill`
  DROP COLUMN `id`,
  DROP INDEX `id_UNIQUE` ,
  DROP PRIMARY KEY,
  ADD UNIQUE INDEX `unique_idx` (`player` ASC, `world` ASC, `victimType` ASC, `victimName` ASC, `weapon` ASC);
ALTER TABLE `stats_playtime`
    DROP COLUMN `id`,
    DROP INDEX `id_UNIQUE` ,
    DROP PRIMARY KEY;
ALTER TABLE `stats_pvp_kills`
  DROP COLUMN `id`,
  DROP INDEX `id_UNIQUE` ,
  DROP PRIMARY KEY,
  ADD UNIQUE INDEX `unique_idx` (`player` ASC, `world` ASC);
ALTER TABLE `stats_pvp_kill_streak`
  DROP COLUMN `id`,
  DROP INDEX `id_UNIQUE` ,
  DROP PRIMARY KEY,
  ADD UNIQUE INDEX `unique_idx` (`player` ASC, `world` ASC);
ALTER TABLE `stats_teleports`
  DROP COLUMN `id`,
  DROP INDEX `id_UNIQUE` ,
  DROP PRIMARY KEY,
  ADD UNIQUE INDEX `unique_idx` (`player` ASC, `world` ASC);
ALTER TABLE `stats_times_joined`
  DROP COLUMN `id`,
  DROP INDEX `id_UNIQUE` ,
  DROP PRIMARY KEY,
  ADD UNIQUE INDEX `unique_idx` (`player` ASC, `world` ASC);
ALTER TABLE `stats_times_kicked`
  DROP COLUMN `id`,
  DROP INDEX `id_UNIQUE` ,
  DROP PRIMARY KEY,
  ADD UNIQUE INDEX `unique_idx` (`player` ASC, `world` ASC);
ALTER TABLE `stats_words_said`
  DROP COLUMN `id`,
  DROP INDEX `id_UNIQUE` ,
  DROP PRIMARY KEY,
  ADD UNIQUE INDEX `unique_idx` (`player` ASC, `world` ASC);
ALTER TABLE `stats_xp_gained`
  DROP COLUMN `id`,
  DROP INDEX `id_UNIQUE` ,
  DROP PRIMARY KEY,
  ADD UNIQUE INDEX `unique_idx` (`player` ASC, `world` ASC);

DELETE
FROM stats_system
WHERE version <= 3;
REPLACE INTO stats_system VALUE (3);
