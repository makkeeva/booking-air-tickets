INSERT INTO authority
VALUES (1, 'ADMIN'),
       (2, 'USER');

INSERT INTO user
VALUES ('admin', '{bcrypt}$2a$10$Q4wAON25LYBgdtXQkE53v.IJYLwIycPLJ6DErsRRn20sEKF5K2HQK', '2023-04-25', 1), /*admin*/
       ('users', '{bcrypt}$2a$10$44WDMVy8GISVVmBtbry7s.BkA7GkLXFFwVOLQ.JrDFmdnTL/zd5XO', '2023-01-25',1); /*users*/

INSERT INTO user_authorities
VALUES (1, 'admin'),
       (2, 'users');