create table Order_Product (
    order_id int references orders(id) on delete cascade,
    product_id int references product(id) on delete cascade
);



