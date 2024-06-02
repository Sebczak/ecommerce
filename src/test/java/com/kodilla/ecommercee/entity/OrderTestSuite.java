package com.kodilla.ecommercee.entity;

import com.kodilla.ecommercee.repository.CartRepository;
import com.kodilla.ecommercee.repository.OrderRepository;
import com.kodilla.ecommercee.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kodilla.ecommercee.entity.OrderStatus.PAID;
import static com.kodilla.ecommercee.entity.OrderStatus.UNPAID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderTestSuite {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void cleanUp() {
        orderRepository.deleteAll();
        cartRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldGetAllOrders() {
        //Given
        User user = new User("test", "test", "test");
        Cart cart = new Cart(new ArrayList<>(), user, BigDecimal.ONE, false);
        Cart cart2 = new Cart(new ArrayList<>(), user, BigDecimal.ONE, true);
        Cart cart3 = new Cart(new ArrayList<>(), user, BigDecimal.ONE, false);
        Cart cart4 = new Cart(new ArrayList<>(), user, BigDecimal.ONE, false);
        userRepository.save(user);
        cartRepository.save(cart);
        cartRepository.save(cart2);
        cartRepository.save(cart3);
        cartRepository.save(cart4);
        Order order = new Order(BigDecimal.ZERO, user.getAddress(), UNPAID, user, cart);
        Order order2 = new Order(BigDecimal.ZERO, user.getAddress(), UNPAID, user, cart2);
        Order order3 = new Order(BigDecimal.ZERO, user.getAddress(), UNPAID, user, cart3);
        Order order4 = new Order(BigDecimal.ZERO, user.getAddress(), UNPAID, user, cart4);

        orderRepository.save(order);
        orderRepository.save(order2);
        orderRepository.save(order3);
        orderRepository.save(order4);

        //When
        List<Order> orders = orderRepository.findAll();

        //Then
        assertEquals(4, orders.size());
    }

    @Test
    void shouldGetOneOrder() {
        //Given
        User user = new User("test", "test", "test");
        Cart cart = new Cart(new ArrayList<>(), user, BigDecimal.ONE, false);
        Cart cart2 = new Cart(new ArrayList<>(), user, BigDecimal.ONE, true);
        userRepository.save(user);
        cartRepository.save(cart);
        cartRepository.save(cart2);
        Order order = new Order(BigDecimal.ZERO, user.getAddress(), UNPAID, user, cart);
        Order order2 = new Order(BigDecimal.ZERO, user.getAddress(), UNPAID, user, cart2);

        orderRepository.save(order);
        orderRepository.save(order2);

        //When
        Optional<Order> expectedOrder = orderRepository.findById(order.getId());

        //Then
        assertTrue(expectedOrder.isPresent());
    }

    @Test
    void shouldCreateNewOrder() {
        //Given
        User user = new User("test", "test", "test");
        Cart cart = new Cart(new ArrayList<>(), user, BigDecimal.ONE, false);
        userRepository.save(user);
        cartRepository.save(cart);
        Order order = new Order(BigDecimal.ZERO, user.getAddress(), UNPAID, user, cart);

        //When
        orderRepository.save(order);
        Optional<Order> expectedOrder = orderRepository.findById(order.getId());

        //Then
        assertTrue(expectedOrder.isPresent());
    }

    @Test
    void shouldUpdateOrder() {
        // Given
        User user = new User("test", "test", "test");
        Cart cart = new Cart(new ArrayList<>(), user, BigDecimal.ONE, false);
        userRepository.save(user);
        cartRepository.save(cart);
        Order order = new Order(BigDecimal.ZERO, user.getAddress(), UNPAID, user, cart);

        orderRepository.save(order);

        // When
        order.setStatus(PAID);
        orderRepository.save(order);
        Optional<Order> expectedOrder = orderRepository.findById(order.getId());

        // Then
        assertEquals(PAID, expectedOrder.get().getStatus());
    }

    @Test
    void shouldDeleteOrder() {
        //Given
        User user = new User("test", "test", "test");
        Cart cart = new Cart(new ArrayList<>(), user, BigDecimal.ONE, false);
        userRepository.save(user);
        cartRepository.save(cart);
        Order order = new Order(BigDecimal.ZERO, user.getAddress(), UNPAID, user, cart);

        orderRepository.save(order);

        Optional<Order> expectedOrder = orderRepository.findById(order.getId());
        assertTrue(expectedOrder.isPresent());

        //When
        orderRepository.delete(order);

        //Then
        assertEquals(Optional.empty(), orderRepository.findById(order.getId()));
        assertNotNull(user);
        assertNotNull(cart);
    }
}
