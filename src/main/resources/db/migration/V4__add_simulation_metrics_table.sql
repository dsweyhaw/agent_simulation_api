CREATE TABLE simulation_metrics (
    id BIGINT NOT NULL AUTO_INCREMENT,
    simulation_run_id BIGINT NOT NULL,
    node_id INTEGER NOT NULL,
    node_name VARCHAR(255),
    cpu_usage DOUBLE NOT NULL,
    ram_usage DOUBLE NOT NULL,
    duration INTEGER NOT NULL,

    PRIMARY KEY ( `id` )
);

ALTER TABLE experiment_results
ADD run_time BIGINT;
