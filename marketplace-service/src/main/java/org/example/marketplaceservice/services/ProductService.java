package org.example.marketplaceservice.services;

import org.example.marketplaceservice.exceptions.ProductNotFoundException;
import org.example.marketplaceservice.models.Product;
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

    public void updateProduct(Product updatedProduct, int id) {
        Optional<Product> product = productRepository.findById(id);
        if(product.isEmpty()) {
            throw new ProductNotFoundException("Product with this id not found");
        }
        updatedProduct.setId(id);
        productRepository.save(updatedProduct);
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
}
