INSERT INTO projects
(id, user_id, name, location, created_at, updated_at, created_by, updated_by)
VALUES
    (12, 1, 'pig farm emission', '/pig-farm-emission', '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn');

INSERT INTO models
(id, name, project_id, user_id, created_at, updated_at, created_by, updated_by)
VALUES
    (221, 'simulator-01-normal-fixed-diet', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (222, 'disease-pig', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (223, 'factor', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (224, 'farm', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (225, 'food-disease-config', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (226, 'food-disease-factor', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (227, 'food-disease-pig', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (228, 'food-water-disease-pig', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (229, 'multi-disease-pig', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (230, 'pig', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (231, 'transmit-disease-config', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (232, 'transmit-disease-factor', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (233, 'transmit-disease-pig', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (234, 'simulator-01-normal', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (235, 'simulator-02-CC', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (236, 'simulator-03-DC', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (237, 'simulator-04-CD', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (238, 'simulator-05-DD', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (239, 'simulator-06-transmit', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (240, 'simulator-07-multi', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (241, 'trough', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (242, 'water-disease-config', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (243, 'water-disease-factor', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (244, 'config', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (245, 'pig-fixed-diet', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (246, 'gas-concentration', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (247, 'simulator-02-CC-fixed-diet', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (248, 'simulator-03-DC-fixed-diet', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (249, 'simulator-04-CD-fixed-diet', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (250, 'simulator-05-DD-fixed-diet', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (251, 'simulator-06-transmit-fixed-diet', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn'),
    (252, 'simulator-07-multi-fixed-diet', 12, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00', 'admin@uet.vn', 'admin@uet.vn');

INSERT INTO experiments
(id, name, model_id, project_id, user_id, created_at, updated_at, created_by, updated_by)
VALUES
    (122,'NormalFixedDiet',221,12,1,'2024-09-01 10:30:00','2024-09-01 10:30:00','admin@uet.vn','admin@uet.vn'),
    (123,'Normal',234,12,1,'2024-09-01 10:30:00','2024-09-01 10:30:00','admin@uet.vn','admin@uet.vn'),
    (124,'CC',235,12,1,'2024-09-01 10:30:00','2024-09-01 10:30:00','admin@uet.vn','admin@uet.vn'),
    (125,'DC',236,12,1,'2024-09-01 10:30:00','2024-09-01 10:30:00','admin@uet.vn','admin@uet.vn'),
    (126,'CD',237,12,1,'2024-09-01 10:30:00','2024-09-01 10:30:00','admin@uet.vn','admin@uet.vn'),
    (127,'DD',238,12,1,'2024-09-01 10:30:00','2024-09-01 10:30:00','admin@uet.vn','admin@uet.vn'),
    (128,'Transmit',239,12,1,'2024-09-01 10:30:00','2024-09-01 10:30:00','admin@uet.vn','admin@uet.vn'),
    (129,'Multi',240,12,1,'2024-09-01 10:30:00','2024-09-01 10:30:00','admin@uet.vn','admin@uet.vn'),
    (130,'CCFixedDiet',247,12,1,'2024-09-01 10:30:00','2024-09-01 10:30:00','admin@uet.vn','admin@uet.vn'),
    (131,'DCFixedDiet',248,12,1,'2024-09-01 10:30:00','2024-09-01 10:30:00','admin@uet.vn','admin@uet.vn'),
    (132,'CDFixedDiet',249,12,1,'2024-09-01 10:30:00','2024-09-01 10:30:00','admin@uet.vn','admin@uet.vn'),
    (133,'DDFixedDiet',250,12,1,'2024-09-01 10:30:00','2024-09-01 10:30:00','admin@uet.vn','admin@uet.vn'),
    (134,'TransmitFixedDiet',251,12,1,'2024-09-01 10:30:00','2024-09-01 10:30:00','admin@uet.vn','admin@uet.vn'),
    (135,'MultiFixedDiet',252,12,1,'2024-09-01 10:30:00','2024-09-01 10:30:00','admin@uet.vn','admin@uet.vn');
