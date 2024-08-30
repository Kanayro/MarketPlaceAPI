package org.example.marketplaceservice.models;

import org.example.marketplaceservice.exceptions.ProductNotFoundException;
import org.example.marketplaceservice.mappers.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Component
@SessionScope
public class Cart {

    private List<ProductInOrder> products = new ArrayList<>();

    public void addProduct(Product product, int count) {

        if(product.isCount() == false) {
            throw new ProductNotFoundException("Product not found in storage");
        }else if (product.getCount()-count < 0) {
            throw new ProductNotFoundException("Product not enough in storage");
        }
        ProductInOrder productInOrder = new ProductInOrder();
        productInOrder.setCount(count);
        productInOrder.setName(product.getName());
        productInOrder.setPrice(product.getPrice()*count);
        products.add(productInOrder);
    }

    public List<ProductInOrder> getCart() {
        return this.products;
    }

    public void clear() {
        this.products.clear();
    }
}
