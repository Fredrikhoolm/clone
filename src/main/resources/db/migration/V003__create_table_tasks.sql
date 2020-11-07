create table tasks (
id serial primary key,
name varchar,
status_id integer null references status(id)
);