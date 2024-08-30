drop table order_product;

create table ProductInOrder (
    id int generated by default as identity primary key,
    order_id int references "order"(id) on delete cascade,
    name varchar(100) not null,
    price int check ( price > 0 ) not null,
    count int check ( count > 0 ) not null

);

alter table ProductInOrder owner to postgres;