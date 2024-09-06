package org.example.marketplaceservice.services;

import org.example.marketplaceservice.exceptions.OrderNotFoundException;
import org.example.marketplaceservice.models.Order;
import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.models.ProductInOrder;
import org.example.marketplaceservice.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    @Transactional
    public void save(Order order) {
        for (var productInOrder : order.getProducts()) {
            productService.updateProduct(productInOrder);
            System.out.println("me");
        }
        System.out.println("me1");
        order.setDateOfCreate(new Date());
        order.setStatus("ASSEMBLY");
        System.out.println("me2");
        orderRepository.save(order);
        System.out.println("me3");
    }

    public Order createOrder(List<ProductInOrder> products, Person person) {
        Order order = new Order();
        products.forEach(productInOrder -> productInOrder.setOrder(order));
        order.setProducts(products);
        order.setPerson(person);
        order.setCost(products.stream().mapToInt(ProductInOrder::getPrice).sum());
        return order;
    }

    public Order getOrder(int id) {
        Optional<Order> order = orderRepository.findById(id);

        if(order.isEmpty()) {
            throw new OrderNotFoundException("Order with this id not found");
        }
        return order.get();
    }

    public List<Order> getOrdersByPerson(Person person) {
        return orderRepository.findOrdersByPerson(person);
    }

    public void updateOrderStatus(String status, int id) {
        Optional<Order> ord = orderRepository.findById(id);

        if(ord.isEmpty()) {
            throw new OrderNotFoundException("Order with this id not found");
        }
        Order order = ord.get();
        order.setStatus(status);
        orderRepository.save(order);
    }
}
