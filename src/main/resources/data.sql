--initial default roles
insert into roles (id, name, description) values ('1', 'ROLE_ADMIN', 'admin');
insert into roles (id, name, description) values ('2', 'ROLE_CLIENT', 'client');

--initial default user: admin and client
insert into users (id, email, is_enabled, password, username) values ('1', 'admin@test.test', '1', '$2a$12$N9BLwSuwgDdJW0VNARLgm.VMb4tbHlzbXJqBdTOwDBD6cTWmOwbKm', 'admin');
insert into users_roles (user_id, role_id) values (1, 1);
insert into users_roles (user_id, role_id) values (1, 2);
insert into users (id, email, is_enabled, password, username) values ('2', 'client@test.test', '1', '$2a$12$uAZaikj89XAPS8EhJoKO9.nyVTy2Vo4CzLiAUjotFz3854I5lrJye', 'client');
insert into users_roles (user_id, role_id) values (2, 2);

--initial news
insert into news (title, content, created_by, modified_by) values ('title', 'content', 'admin', 'admin');