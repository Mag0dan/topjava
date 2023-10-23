TRUNCATE meals CASCADE;
TRUNCATE user_role CASCADE;
TRUNCATE users CASCADE;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin'),
       ('Guest', 'guest@gmail.com', 'guest');

INSERT INTO user_role (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (user_id, description, calories, date_time)
VALUES (100000, 'Завтрак', 500, '30-01-2020 10:00'),
       (100000, 'Обед', 1000, '30-01-2020 13:00'),
       (100000, 'Ужин', 500, '30-01-2020 20:00'),
       (100000, 'Еда на граничное значение', 100, '31-01-2020 00:00'),
       (100000, 'Завтрак', 1000, '31-01-2020 10:00'),
       (100000, 'Обед', 500, '31-01-2020 13:00'),
       (100000, 'Ужин', 410, '31-01-2020 20:00');

INSERT INTO meals (user_id, description, calories, date_time)
VALUES (100001, 'Админ Завтрак', 500, '30-01-2020 10:00'),
       (100001, 'Админ Обед', 1000, '30-01-2020 13:00'),
       (100001, 'Админ Ужин', 500, '30-01-2020 20:00'),
       (100001, 'Админ Еда на граничное значение', 100, '31-01-2020 00:00'),
       (100001, 'Админ Завтрак', 1000, '31-01-2020 10:00'),
       (100001, 'Админ Обед', 500, '31-01-2020 13:00'),
       (100001, 'Админ Ужин', 410, '31-01-2020 20:00');
