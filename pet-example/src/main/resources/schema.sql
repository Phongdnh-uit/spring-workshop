create table if not exists dog (
    id serial primary key,
    name varchar(255) not null,
    owner text not null,
    description text not null
);