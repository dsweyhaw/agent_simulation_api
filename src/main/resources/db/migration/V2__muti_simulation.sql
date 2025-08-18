CREATE TABLE pigpen_sync_runs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    run_id BIGINT NOT NULL,
    pigpen_id INTEGER NOT NULL,
    cycle INTEGER NOT NULL,
    is_complete BOOL DEFAULT FALSE,

    PRIMARY KEY (`id`)
);

CREATE TABLE pigpen_daily (
    id BIGINT NOT NULL AUTO_INCREMENT,
    run_id BIGINT NOT NULL,
    pigpen_id INTEGER NOT NULL,
    day INTEGER NOT NULL,
    total_pigs INTEGER NOT NULL,
    unexposed_count INTEGER NOT NULL,
    exposed_count INTEGER NOT NULL,
    infected_count INTEGER NOT NULL,
    recovered_count INTEGER NOT NULL,

    PRIMARY KEY ( `id` )
);
