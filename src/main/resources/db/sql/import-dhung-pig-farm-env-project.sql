INSERT INTO projects
(id, user_id, name, location, created_at, updated_at, created_by, updated_by)
VALUES
    (3, 1, 'pig farm env', '/pig-farm-env', '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn');

INSERT INTO models
(id, name, project_id, user_id, created_at, updated_at, created_by, updated_by)
VALUES
    (300, 'complex-pig', 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (301, 'config', 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (302, 'factor', 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (303, 'farm', 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (304, 'hierarchy', 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (305, 'pig', 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (306, 'rank-pig', 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (307, 'simulator-01', 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (308, 'simulator-qui-autumn', 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (309, 'simulator-qui-summer', 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (310, 'simulator-rena-autumnl', 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (311, 'simulator-rena-summer', 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (312, 'trough', 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn');

INSERT INTO experiments
(id, name, model_id, project_id, user_id, created_at, updated_at, created_by, updated_by)
VALUES
    (201, 'Normal', 307, 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (202, 'Autumn', 308, 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (203, 'Summer', 309, 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (204, 'Autumn', 310, 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (205, 'Summer', 311, 3, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn');
