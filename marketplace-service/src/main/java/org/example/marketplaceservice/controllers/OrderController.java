package org.example.marketplaceservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.marketplaceservice.dto.OrderDTO;
import org.example.marketplaceservice.dto.OrderMessageDTO;
import org.example.marketplaceservice.exceptions.CartIsEmptyException;
import org.example.marketplaceservice.exceptions.ErrorResponse;
import org.example.marketplaceservice.exceptions.OrderNotFoundException;
import org.example.marketplaceservice.mappers.OrderMapper;
import org.example.marketplaceservice.models.Cart;
import org.example.marketplaceservice.models.Order;
import org.example.marketplaceservice.models.Person;
import org.example.marketplaceservice.security.JWTUtil;
import org.example.marketplaceservice.services.OrderService;
import org.example.marketplaceservice.services.PersonService;
import org.example.marketplaceservice.util.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final JWTUtil jwtUtil;
    private final OrderService orderService;
    private final PersonService personService;
    private final OrderMapper orderMapper;
    private final KafkaProducer producer;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    public OrderController(JWTUtil jwtUtil, OrderService orderService, PersonService personService, OrderMapper orderMapper, KafkaProducer producer) {
        this.jwtUtil = jwtUtil;
        this.orderService = orderService;
        this.personService = personService;
        this.orderMapper = orderMapper;
        this.producer = producer;
    }

    @GetMapping("/create")
    public ResponseEntity<HttpStatus> createOrder(HttpServletRequest request, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("user"); // Получение объекта Cart из сессии пользователя.
        if(cart.getCart().isEmpty()) { // Проверка, пуста ли корзина.
            logger.warn("User's cart is empty");
            throw new CartIsEmptyException("Your cart is empty"); // Если корзина пуста, выбрасывается исключение.
        }
        Person person = personService.findByLogin( // Найти пользователя по логину.
                jwtUtil.validateTokenAndRetrieveClaim(jwtUtil.getJWT(request)).getLogin()
        );
        Order order = orderService.createOrder(cart.getCart(), person); // Создание нового заказа на основе товаров в корзине.
        orderService.save(order); // Сохранение заказа в базе данных.
        producer.sendMessage(new OrderMessageDTO(order.getId(), order.getStatus())); // Отправка сообщения о заказе в Kafka.
        cart.clear(); // Очистка корзины.
        logger.info("Order created successfully");
        return ResponseEntity.ok(HttpStatus.OK); // Возврат успешного ответа.
    }

    @GetMapping("/get")
    public List<OrderDTO> getOrders(HttpServletRequest request) {
        Person person = personService.findByLogin( // Получение пользователя по логину из JWT.
                jwtUtil.validateTokenAndRetrieveClaim(jwtUtil.getJWT(request)).getLogin()
        );
        List<Order> orders = orderService.getOrdersByPerson(person); // Получение всех заказов пользователя.
        logger.info("Orders received successfully");
        // Преобразование заказов в список DTO и возврат его.
        return orders.stream().map(orderMapper::convertToOrderDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}/get") // Обрабатывает GET-запросы на получение заказа по его ID.
    public OrderDTO getOrder(@PathVariable("id") int id) {
        logger.info("Orders received successfully");
        return orderMapper.convertToOrderDTO(orderService.getOrder(id)); // Возвращает заказ по ID, преобразованный в DTO.
    }

    // Обработчики исключений

    @ExceptionHandler // Обработка исключений, выбрасываемых в методах данного контроллера.
    private ResponseEntity<ErrorResponse> handleException(UsernameNotFoundException e){
        // Создание объекта ответа с ошибкой.
        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // Возврат ответа с ошибкой и статусом 400 BAD_REQUEST.
    }

    @ExceptionHandler // Обработка исключений для заказа, если он не найден.
    private ResponseEntity<ErrorResponse> handleException(OrderNotFoundException e){
        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // Возврат ответа с ошибкой и статусом 404 NOT_FOUND.
    }

    @ExceptionHandler // Обработка исключений для пустой корзины.
    private ResponseEntity<ErrorResponse> handleException(CartIsEmptyException e){
        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // Возврат ответа с ошибкой и статусом 400 BAD_REQUEST.
    }
}
