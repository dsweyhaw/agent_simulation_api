-- nodes table --
CREATE TABLE IF NOT EXISTS nodes (
    id INTEGER NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    role TINYINT NOT NULL,
    host VARCHAR(255) NOT NULL,
    port INTEGER NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    PRIMARY KEY (`id`)
);

-- users table --
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fullname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role TINYINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (`id`)
);

-- projects table --
CREATE TABLE IF NOT EXISTS projects (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (`id`),
    CONSTRAINT fk_projects_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX idx_projects_user_id ON projects(user_id);

-- models table --
CREATE TABLE IF NOT EXISTS models (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (`id`),
    CONSTRAINT fk_models_project_id FOREIGN KEY (project_id) REFERENCES projects(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_models_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX idx_models_project_id_user_id ON models(project_id, user_id);

-- experiments table --
CREATE TABLE IF NOT EXISTS experiments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    model_id BIGINT,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (`id`),
    CONSTRAINT fk_experiments_model_id FOREIGN KEY (model_id) REFERENCES models(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_experiments_project_id FOREIGN KEY (project_id) REFERENCES projects(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_experiments_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX idx_experiments_model_id_project_id_user_id ON experiments(model_id, project_id, user_id);

-- experiment_results table --
CREATE TABLE IF NOT EXISTS experiment_results (
    id BIGINT NOT NULL AUTO_INCREMENT,
    final_step INTEGER NOT NULL,
    experiment_id BIGINT NOT NULL,
    location VARCHAR(300),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    node_id INTEGER NOT NULL,
    status TINYINT NOT NULL,
    run_command_pid BIGINT,
    number BIGINT,
    simulation_run_id BIGINT,
    PRIMARY KEY (`id`),
    CONSTRAINT fk_experiment_results_experiment_id FOREIGN KEY (experiment_id) REFERENCES experiments(id)
    ON UPDATE RESTRICT ON DELETE CASCADE,
    CONSTRAINT fk_experiment_results_node_id FOREIGN KEY (node_id) REFERENCES nodes(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX idx_experiment_results_experiment_id ON experiment_results(experiment_id);

-- experiment_result_categories table
CREATE TABLE IF NOT EXISTS experiment_result_category_seq (
    next_val BIGINT NOT NULL
);
INSERT INTO experiment_result_category_seq VALUES (1);
CREATE TABLE IF NOT EXISTS experiment_result_categories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    experiment_result_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (`id`),
    CONSTRAINT fk_experiment_result_categories_experiment_result_id FOREIGN KEY (experiment_result_id)
    REFERENCES experiment_results(id) ON UPDATE RESTRICT ON DELETE CASCADE
    );
CREATE INDEX idx_experiment_result_categories_experiment_result_id ON experiment_result_categories(experiment_result_id);

-- experiment_result_images table
CREATE TABLE IF NOT EXISTS experiment_result_image_seq (
    next_val BIGINT NOT NULL
);
INSERT INTO experiment_result_image_seq VALUES (1);
CREATE TABLE IF NOT EXISTS experiment_result_images (
    id BIGINT NOT NULL AUTO_INCREMENT,
    experiment_result_id BIGINT NOT NULL,
    experiment_result_category_id BIGINT,
    name VARCHAR(255),
    location VARCHAR(300),
    extension VARCHAR(20),
    step INTEGER,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (`id`),
    CONSTRAINT fk_experiment_result_images_experiment_result_id FOREIGN KEY (experiment_result_id)
    REFERENCES experiment_results(id) ON UPDATE RESTRICT ON DELETE CASCADE,
    CONSTRAINT fk_experiment_result_images_experiment_result_category_id FOREIGN KEY (experiment_result_category_id)
    REFERENCES experiment_result_categories(id) ON UPDATE RESTRICT ON DELETE CASCADE
);
CREATE INDEX idx_experiment_result_images_exp_res_id_exp_res_category_id ON
experiment_result_images(experiment_result_id, experiment_result_category_id);

-- simulation_runs table
CREATE TABLE IF NOT EXISTS simulation_runs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (`id`)
);
