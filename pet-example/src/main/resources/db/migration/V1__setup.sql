create table if not exists person (
                                      id serial primary key
);

create table if not exists dog (
                                   id serial primary key,
                                   name text not null,
                                   own text null,
                                   description text not null,
                                   person bigint references person(id)
    );

