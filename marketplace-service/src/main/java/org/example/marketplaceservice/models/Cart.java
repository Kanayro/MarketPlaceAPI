package org.example.marketplaceservice.models;

import org.example.marketplaceservice.exceptions.ProductNotEnoughException;
import org.example.marketplaceservice.exceptions.ProductNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

//Класс корзины, которая хранит список продуктов пользователя до совершения заказа
@Component
@SessionScope //Означает, что бин данного класса будет распростроняться только на конкретную сессию
public class Cart {

    //Список продуктов
    private List<ProductInOrder> products = new ArrayList<>();

    //Метод добавления продуктов в корзину
    public void addProduct(Product product, int count) {
        System.out.println(product.getCount());
        if(product.getIsCount() == false) {
            throw new ProductNotFoundException("Product not found in storage");
        }else if (product.getCount()-count < 0) {
            throw new ProductNotEnoughException("Product not enough in storage");
        }
        ProductInOrder productInOrder = new ProductInOrder();
        productInOrder.setCount(count);
        productInOrder.setName(product.getName());
        productInOrder.setPrice(product.getPrice()*count);
        products.add(productInOrder);
    }

    //Получения списка продуктов из корзины
    public List<ProductInOrder> getCart() {
        return this.products;
    }

    //Очистка корзины
    public void clear() {
        this.products.clear();
    }
}
