INSERT INTO USERS (name, email, password)
VALUES ('User', 'user@yandex.ru', '{noop}password'),
       ('Admin', 'admin@gmail.com', '{noop}admin');

INSERT INTO USER_ROLES (role, user_id)
VALUES ('USER', 1),
       ('ADMIN', 2),
       ('USER', 2);

INSERT INTO RESTAURANT (name, address, phone, last_vote_date)
VALUES ('Bigrest', 'Bolshaya str. 12', '888-88-88', now()),
       ('Middlecafe', 'Middle str. 5', '555-55-55', now()),
       ('Littleponchik', 'Small str. 3', '333-33-33', now());

INSERT INTO VOTE (name, restaurant_id, date, menu, votes)
VALUES ('Bigrest', 1, now(), 'Paste, tiramisu, wine - 20.00', 5),
       ('Middlecafe', 2, now(), 'Soup, cutlet, compote - 10.00', 3),
       ('Littleponchik', 3, now(), 'Donut - 1.00, Tea - 0.50', 2);

INSERT INTO DISH (name, restaurant_id, date, price)
VALUES ('Paste, tiramisu, wine', 1, now(), 20.00),
       ('Soup, cutlet, compote', 2, now(), 10.00),
       ('Donut', 3, now(), 1.00),
       ('Tea', 3, now(), 0.50);