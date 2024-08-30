package org.example.marketplaceservice.services;

import org.example.marketplaceservice.exceptions.OrderNotFoundException;
import org.example.marketplaceservice.models.Order;
import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.models.Product;
import org.example.marketplaceservice.models.ProductInOrder;
import org.example.marketplaceservice.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void save(Order order) {
        order.setDateOfCreate(new Date());
        orderRepository.save(order);
    }

    public Order createOrder(List<ProductInOrder> products, Person person) {
        Order order = new Order();
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




}
