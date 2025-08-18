CREATE TABLE pig_data_daily (
    id BIGINT NOT NULL AUTO_INCREMENT,
    run_id BIGINT NOT NULL,
    pigpen_id INTEGER NOT NULL,
    pig_id INTEGER NOT NULL,
    day INTEGER NOT NULL,
    weight DOUBLE NOT NULL,
    cfi DOUBLE NOT NULL,
    dfi DOUBLE NOT NULL,
    target_cfi DOUBLE NOT NULL,
    target_dfi DOUBLE NOT NULL,
    eat_count INTEGER,
    excrete_count INTEGER,
    seir TINYINT,

    PRIMARY KEY ( `id` )
);
