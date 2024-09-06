package org.example.marketplaceservice.services;

import org.example.marketplaceservice.exceptions.ProductNotFoundException;
import org.example.marketplaceservice.models.Product;
import org.example.marketplaceservice.models.ProductInOrder;
import org.example.marketplaceservice.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public Product findById(int id) {
        Optional<Product> product = productRepository.findById(id);
        if(product.isEmpty()) {
            throw new ProductNotFoundException("Product with this id not found");
        }
        return product.get();
    }

    public void updateProduct(ProductInOrder productInOrder) {
        Optional<Product> prod = productRepository.findByName(productInOrder.getName());
        System.out.println("prdo1");
        if(prod.isEmpty()) {
            throw new ProductNotFoundException("Product with this name not found ");
        }
        Product product = prod.get();
        product.setCount(product.getCount()-productInOrder.getCount());
        if(product.getCount() == 0) {
            product.setCount(false);
        }
        productRepository.save(product);
    }

    public void delete(int id){
        Optional<Product> product = productRepository.findById(id);
        if(product.isEmpty()) {
            throw new ProductNotFoundException("Product with this id not found");
        }
        productRepository.delete(product.get());
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findProductByName(String name) {
        return productRepository.findByName(name);
    }
}
