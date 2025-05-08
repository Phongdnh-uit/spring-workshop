insert into users( username, password, enabled) values ('test', '$2a$10$EevXu.5albkNU6OEl/oVheasFdt7/zfsCVK5Rkmz88jrzcbhG1Pie',true);
insert into users( username, password, enabled) values ('test1', '$2a$10$oEg28yUWcwDpqmERLGMKE.l1XDEyh9POUu5cDc1K2fcfx68ozpM2K',true);
insert into users( username, password, enabled) values ('accountant', '$2a$10$oEg28yUWcwDpqmERLGMKE.l1XDEyh9POUu5cDc1K2fcfx68ozpM2K',true);

insert into authorities (username, authority) values ('test', 'ROLE_USER');
insert into authorities (username, authority) values ('test1', 'ROLE_USER');
insert into authorities (username, authority) values ('test1', 'ROLE_ADMIN');
insert into authorities (username, authority) values ('accountant', 'ROLE_ACCOUNTANT');
