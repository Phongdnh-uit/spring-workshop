drop table if exists dog;
create table dog (
    id serial primary key,
    name varchar(255) not null,
    owner text null,
    description text null
);