INSERT INTO projects
(id, user_id, name, location, created_at, updated_at, created_by, updated_by)
VALUES
    (2, 1, 'pig farm multi simulation', '/pig-farm-multi-simulation', '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn');

INSERT INTO models
(id, name, project_id, user_id, created_at, updated_at, created_by, updated_by)
VALUES
    (24, 'config', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (25, 'disease-pig', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (26, 'factor', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (27, 'farm', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (28, 'food-disease-config', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (29, 'food-disease-factor', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (30, 'food-disease-pig', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (31, 'food-water-disease-pig', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (32, 'multi-disease-pig', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (33, 'pig', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (34, 'transmit-disease-config', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (35, 'transmit-disease-factor', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (36, 'transmit-disease-pig', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (37, 'simulator-01', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (38, 'simulator-02', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (39, 'simulator-03', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (40, 'simulator-04', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (41, 'simulator-05', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (42, 'simulator-06', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (43, 'simulator-07', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (44, 'trough', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (45, 'water-disease-config', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (46, 'water-disease-factor', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (47, 'multi-simulator', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (48, 'pigpen-2-parallel', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (49, 'pigpen-3-parallel', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (50, 'pigpen-1-distributed', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (51, 'pigpen-2-distributed', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (52, 'pigpen-3-distributed', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (53, 'pigpen-4-distributed', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (54, 'pigpen-5-distributed', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (55, 'pigpen-6-distributed', 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn');


INSERT INTO experiments
(id, name, model_id, project_id, user_id, created_at, updated_at, created_by, updated_by)
VALUES
    (8, 'MultiSimulation', 47, 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (9, 'Pigpen2', 48, 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (10, 'Pigpen3', 49, 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (11, 'Pigpen1', 50, 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (12, 'Pigpen2', 51, 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (13, 'Pigpen3', 52, 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (14, 'Pigpen4', 53, 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (15, 'Pigpen5', 54, 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (16, 'Pigpen6', 55, 2, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn');
