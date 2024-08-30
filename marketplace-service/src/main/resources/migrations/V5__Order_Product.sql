create table Order_Product (
    order_id int references "Order"(id) on delete cascade,
    product_id int references product(id) on delete cascade
);

alter table Order_Product owner to postgres;

