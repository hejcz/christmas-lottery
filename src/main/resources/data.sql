INSERT INTO users (id, login, name, surname, email, password, system_role) VALUES
(1, 'User1', 'Name1', 'Surname1', 'rubin94+01@`gmail.com', '$2a$10$qOWF.yhmrcBy8KE3Y4mjLOcEEIa6Coo471fMmPoo.iYwNFs7tVlH6', 0),
(2, 'User2', 'Name2', 'Surname2', 'rubin94+02@`gmail.com', '$2a$10$rCqJVYCnJ9tcxW5NalEyNOrUNfG/2adj3chguUQi2FPpRphetgOnC', 0),
(3, 'User3', 'Name3', 'Surname3', 'rubin94+03@`gmail.com', '$2a$10$IoXLf4nHXviOcn1cAtGyMObDBL4O6mpKJPYbekCPiQCbm6vslkQQO', 0),
(4, 'User4', 'Name4', 'Surname4', 'rubin94+04@`gmail.com', '$2a$10$bFQ4o/BYOPSDqjYRBPXVcu2hpZl0I0Olh7zWJ0J/LvQBOgCZ.CDpO', 0),
(5, 'Admin', '', '', 'rubin94+05@gmail.com', '$2a$10$EWvtUxADjhuTDGnQWiMg/eeoSrYngRsyRuvnYdX/I57UQptG9YTXS', 1);

INSERT INTO matches (id, creation_date, giver_id, recipient_id, locked) VALUES
(1, '2018-04-03', 1, 2, false),
(2, '2018-04-03', 2, 3, false),
(3, '2018-04-03', 3, 4, false),
(4, '2018-04-03', 4, 1, false);

INSERT INTO wishes (id, creation_date, text, power, recipient_id) VALUES
(1, '2018-04-03', 'Płyn do naczyń', 2, 1),
(2, '2018-04-03', 'Półka na książki', 3, 1);

SELECT setval('hibernate_sequence', 50);