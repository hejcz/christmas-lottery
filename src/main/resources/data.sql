INSERT INTO users (id, login, name, surname, email, password, system_role) VALUES
(1, 'jrubin1', 'Julian1', 'Rubin1', 'rubin94+01@gmail.com', '$2a$10$IOmaHx4I4qRgb.Nxdu6aiO/PQRQMN/uKEH.j51xSX/uIliQaP5Aju', 0),
(2, 'jrubin2', 'Julian2', 'Rubin2', 'rubin94+02@gmail.com', '$2a$10$IOmaHx4I4qRgb.Nxdu6aiO/PQRQMN/uKEH.j51xSX/uIliQaP5Aju', 0),
(3, 'jrubin3', 'Julian3', 'Rubin3', 'rubin94+03@gmail.com', '$2a$10$IOmaHx4I4qRgb.Nxdu6aiO/PQRQMN/uKEH.j51xSX/uIliQaP5Aju', 0),
(4, 'jrubin4', 'Julian4', 'Rubin4', 'rubin94+04@gmail.com', '$2a$10$IOmaHx4I4qRgb.Nxdu6aiO/PQRQMN/uKEH.j51xSX/uIliQaP5Aju', 0),
(5, 'jrubin5', 'Julian5', 'Rubin5', 'rubin94+05@gmail.com', '$2a$10$IOmaHx4I4qRgb.Nxdu6aiO/PQRQMN/uKEH.j51xSX/uIliQaP5Aju', 1);

INSERT INTO matches (id, creation_date, giver_id, recipient_id) VALUES
(1, '2018-04-03', 1, 2),
(2, '2018-04-03', 2, 3),
(3, '2018-04-03', 3, 4),
(4, '2018-04-03', 4, 1);

INSERT INTO wishes (id, creation_date, text, power, recipient_id, locked) VALUES
(1, '2018-04-03', 'Płyn do naczyń', 2, 1, false),
(2, '2018-04-03', 'Półka na książki', 3, 1, false);

SELECT setval('hibernate_sequence', 50);