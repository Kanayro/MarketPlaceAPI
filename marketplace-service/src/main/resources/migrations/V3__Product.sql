create table Product (
    id int generated by default as identity primary key,
    name varchar(100) not null unique,
    price int check ( price > 0 ) not null,
    count int check ( count > 0 ) not null,
    isCount boolean not null
);

alter table Product owner to postgres;