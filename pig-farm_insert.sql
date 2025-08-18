-- Insert into projects table
INSERT INTO projects
(id, user_id, name, location, created_at, updated_at, created_by, updated_by)
VALUES
    (1, 1, 'pig-farm', '/pig-farm', '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn');

-- Insert into models table
INSERT INTO models
(id, name, project_id, user_id, created_at, updated_at, created_by, updated_by)
VALUES
    (1, 'base-pigpen-model', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (2, 'config', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (3, 'database-helper-2', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (4, 'database-helper-3', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (5, 'database-helper', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (6, 'database-util', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (7, 'disease-pig', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (8, 'factor', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (9, 'farm', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (10, 'food-disease-config', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (11, 'food-disease-factor', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (12, 'food-disease-pig', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (13, 'food-water-disease-pig', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (14, 'multi-disease-pig', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (15, 'multi-simulator', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (16, 'pig', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (17, 'simulator-01', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (18, 'simulator-02', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (19, 'simulator-03', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (20, 'simulator-04', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (21, 'simulator-05', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (22, 'simulator-06', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (23, 'simulator-07', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (24, 'transmit-agent', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (25, 'transmit-disease-agent', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (26, 'transmit-disease-config', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (27, 'transmit-disease-factor', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (28, 'transmit-disease-pig', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (29, 'trough', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (30, 'water-disease-config', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (31, 'water-disease-factor', 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
;

-- Insert into experiments table
INSERT INTO experiments
(id, name, model_id, project_id, user_id, created_at, updated_at, created_by, updated_by)
VALUES
    (1, 'MultiSimulation', 15, 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (2, 'Normal', 17, 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (3, 'CC', 18, 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (4, 'DC', 19, 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (5, 'CD', 20, 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (6, 'DD', 21, 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (7, 'Transmit', 22, 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
,
    (8, 'Multi', 23, 1, 1, '2025-05-06 14:40:08', '2025-05-06 14:40:08', 'admin@uet.vn', 'admin@uet.vn')
;
