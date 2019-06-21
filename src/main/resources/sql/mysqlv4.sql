CREATE TABLE IF NOT EXISTS stats_signs
(
    id    BINARY(16) PRIMARY KEY,
    world BINARY(16) NOT NULL,
    x     INT        NOT NULL,
    y     INT        NOT NULL,
    z     INT        NOT NULL,
    spec  TEXT       NOT NULL
);

UPDATE stats_trades_performed
SET price=REPLACE(REPLACE(REPLACE(price, '{namespace:', ''), 'key:"', ':'), '"},', '",');
UPDATE stats_trades_performed
SET item=REPLACE(REPLACE(REPLACE(item, '{namespace:', ''), 'key:"', ':'), '"},', '",');

REPLACE INTO stats_system VALUE (4);
