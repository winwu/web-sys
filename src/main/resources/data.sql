--initial default roles
insert into roles (id, name, description) values ('1', 'ROLE_SYSTEM_ADMIN', 'super administrator');
insert into roles (id, name, description) values ('2', 'ROLE_ADMIN', 'registered admin user');
insert into roles (id, name, description) values ('3', 'ROLE_ADMIN_MARKETING', 'marketing team member');
insert into roles (id, name, description) values ('4', 'ROLE_ADMIN_SALES', 'sales team member');

--initial default user: system admin, general admin, marketing admin, sales admin, default pwd is 12345678
insert into users (id, email, is_enabled, password, username) values ('1', 'superadmin@test.test', '1', '$2a$12$1JmOxwJPvd6uDigjVQUYkuZmUqC1X83IKsJxDNmsPvEiExgltNp6y', 'superadmin');
insert into users_roles (user_id, role_id) values (1, 1);

insert into users (id, email, is_enabled, password, username) values ('2', 'admin@test.test', '1', '$2a$12$N9BLwSuwgDdJW0VNARLgm.VMb4tbHlzbXJqBdTOwDBD6cTWmOwbKm', 'admin');
insert into users_roles (user_id, role_id) values (2, 2);

insert into users (id, email, is_enabled, password, username) values ('3', 'marketing@test.test', '1', '$2a$12$dEo60RbnS4IWQX81t3.WjuZZZjkCrArHm6M7A07Zuh/VogVP335ki', 'marketing');
insert into users_roles (user_id, role_id) values (3, 3);

insert into users (id, email, is_enabled, password, username) values ('4', 'sales@test.test', '1', '$2a$12$ItAPbA8pkj5kB0LNav6W6OIn3.EftltycrtGJsGvN7z68SiZjs2OC', 'salesteam');
insert into users_roles (user_id, role_id) values (4, 4);

--initial default permission
insert into permissions (id, resource, name) values ('1', 'users', 'admin-users-read');
insert into permissions (id, resource, name) values ('2', 'users', 'admin-users-write');
insert into permissions (id, resource, name) values ('3', 'users', 'admin-users-delete');
insert into permissions (id, resource, name) values ('4', 'users', 'admin-users-all');
insert into permissions (id, resource, name) values ('5', 'news', 'news-read');
insert into permissions (id, resource, name) values ('6', 'news', 'news-write');
insert into permissions (id, resource, name) values ('7', 'news', 'news-delete');
insert into permissions (id, resource, name) values ('8', 'news', 'news-users-all');
insert into permissions (id, resource, name) values ('9', 'product', 'product-read');
insert into permissions (id, resource, name) values ('10', 'product', 'product-write');
insert into permissions (id, resource, name) values ('11', 'product', 'product-delete');
insert into permissions (id, resource, name) values ('12', 'product', 'product-all');

--initial default roles_permissions
--permissions for ROLE_SYSTEM_ADMIN
insert into roles_permissions (role_id, permission_id) values (1, 1);
insert into roles_permissions (role_id, permission_id) values (1, 2);
insert into roles_permissions (role_id, permission_id) values (1, 3);
insert into roles_permissions (role_id, permission_id) values (1, 4);
insert into roles_permissions (role_id, permission_id) values (1, 5);
insert into roles_permissions (role_id, permission_id) values (1, 6);
insert into roles_permissions (role_id, permission_id) values (1, 7);
insert into roles_permissions (role_id, permission_id) values (1, 8);
insert into roles_permissions (role_id, permission_id) values (1, 9);
insert into roles_permissions (role_id, permission_id) values (1, 10);
insert into roles_permissions (role_id, permission_id) values (1, 11);
insert into roles_permissions (role_id, permission_id) values (1, 12);
--permissions for ROLE_ADMIN
insert into roles_permissions (role_id, permission_id) values (2, 5);
insert into roles_permissions (role_id, permission_id) values (2, 6);
insert into roles_permissions (role_id, permission_id) values (2, 7);
insert into roles_permissions (role_id, permission_id) values (2, 8);
insert into roles_permissions (role_id, permission_id) values (2, 9);
insert into roles_permissions (role_id, permission_id) values (2, 10);
insert into roles_permissions (role_id, permission_id) values (2, 11);
insert into roles_permissions (role_id, permission_id) values (2, 12);
--permissions for ROLE_ADMIN_MARKETING
insert into roles_permissions (role_id, permission_id) values (3, 5);
insert into roles_permissions (role_id, permission_id) values (3, 6);
insert into roles_permissions (role_id, permission_id) values (3, 7);
insert into roles_permissions (role_id, permission_id) values (3, 8);
--permissions for ROLE_ADMIN_SALES
insert into roles_permissions (role_id, permission_id) values (4, 5);
insert into roles_permissions (role_id, permission_id) values (4, 6);
insert into roles_permissions (role_id, permission_id) values (4, 7);
insert into roles_permissions (role_id, permission_id) values (4, 8);
insert into roles_permissions (role_id, permission_id) values (4, 9);
insert into roles_permissions (role_id, permission_id) values (4, 10);
insert into roles_permissions (role_id, permission_id) values (4, 11);
insert into roles_permissions (role_id, permission_id) values (4, 12);


--initial news
insert into news (title, content, created_by, modified_by) values ('title', 'content', 'admin', 'admin');