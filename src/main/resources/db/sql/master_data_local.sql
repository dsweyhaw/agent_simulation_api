INSERT INTO users
    (id, fullname, email, password, role, created_at, updated_at, created_by, updated_by)
VALUES
    (1, 'admin', 'admin@uet.vn', '$2a$10$KlieR6MHNv88eZYteM9b9eue.5Y1/okJyDUm9uzYIWqtMQJ2mDhNq', 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn');

INSERT INTO projects
    (id, user_id, name, location, created_at, updated_at, created_by, updated_by)
VALUES
    (1, 1, 'pig farm', '/pig-farm', '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn');

INSERT INTO models
    (id, name, project_id, user_id, created_at, updated_at, created_by, updated_by)
VALUES
    (1, 'config', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (2, 'disease-pig', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (3, 'factor', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (4, 'farm', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (5, 'food-disease-config', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (6, 'food-disease-factor', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (7, 'food-disease-pig', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (8, 'food-water-disease-pig', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (9, 'multi-disease-pig', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (10, 'pig', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (11, 'transmit-disease-config', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (12, 'transmit-disease-factor', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (13, 'transmit-disease-pig', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (14, 'simulator-01', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (15, 'simulator-02', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (16, 'simulator-03', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (17, 'simulator-04', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (18, 'simulator-05', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (19, 'simulator-06', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (20, 'simulator-07', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (21, 'trough', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (22, 'water-disease-config', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (23, 'water-disease-factor', 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn');

INSERT INTO experiments
    (id, name, model_id, project_id, user_id, created_at, updated_at, created_by, updated_by)
VALUES
    (1, 'Normal', 14, 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (2, 'CC', 15, 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (3, 'DC', 16, 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (4, 'CD', 17, 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (5, 'DD', 18, 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (6, 'Transmit', 19, 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (7, 'Multi', 20, 1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn');
