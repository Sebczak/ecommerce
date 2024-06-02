package com.kodilla.ecommercee.entity;

import com.kodilla.ecommercee.repository.CartRepository;
import com.kodilla.ecommercee.repository.OrderRepository;
import com.kodilla.ecommercee.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class UserTestSuite {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartRepository cartRepository;

    @AfterEach
    public void cleanUp() {
        orderRepository.deleteAll();
        cartRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldGetAllUsers() {
        //given
        User user1 = new User("Username 1","Password 1","Address 1");
        User user2 = new User("Username 2","Password 2","Address 2");
        User user3 = new User("Username 3","Password 3","Address 3");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        //when
        List<User> users = userRepository.findAll();

        //then
        assertEquals(3, users.size());
    }

    @Test
    void shouldGetUserById() {
        //given
        User user1 = new User("Username 1","Password 1","Address 1");
        User user2 = new User("Username 2","Password 2","Address 2");
        userRepository.save(user1);
        userRepository.save(user2);

        //when
        Optional<User> expectedUser1 = userRepository.findById(user1.getId());

        //then
        assertThat(expectedUser1).isNotNull();
        assertEquals(expectedUser1.get().getId(), user1.getId());
        assertNotEquals(expectedUser1.get().getId(), user2.getId());
    }

    @Test
    void shouldDeleteUser() {
        //given
        User user1 = new User("Username 1","Password 1","Address 1");
        User user2 = new User("Username 2","Password 2","Address 2");
        User user3 = new User("Username 3","Password 3","Address 3");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        Cart cart1 = new Cart();
        cart1.setUser(user2);
        cartRepository.save(cart1);
        Cart cart2 = new Cart();
        cart2.setUser(user2);
        cartRepository.save(cart2);

        Order order1 = new Order(new BigDecimal(100),"ShippingAddress 1", OrderStatus.UNPAID,user2,cart1);
        Order order2 = new Order(new BigDecimal(200),"ShippingAddress 2",OrderStatus.UNPAID,user2,cart2);
        orderRepository.save(order1);
        orderRepository.save(order2);

        //when
        userRepository.deleteById(user2.getId());

        //then
        Long sizeOfUser = userRepository.count();
        Long sizeOfOrder = orderRepository.count();
        Long sizeOfCart = cartRepository.count();
        Optional<User> expectedNullUser = userRepository.findById(user2.getId());
        Optional<Order> expectedNullOrder = orderRepository.findById(order1.getId());
        Optional<Cart> expectedNullCart = cartRepository.findById(cart2.getId());

        assertEquals(2, sizeOfUser);
        assertEquals(0, sizeOfOrder);
        assertEquals(0, sizeOfCart);
        assertFalse(expectedNullUser.isPresent());
        assertFalse(expectedNullOrder.isPresent());
        assertFalse(expectedNullCart.isPresent());
    }

    @Test
    void shouldCreateUser() {
        //given
        User user = new User("New username","New password","New address");

        Cart cart1 = new Cart();
        cart1.setUser(user);
        Cart cart2 = new Cart();
        cart2.setUser(user);

        Order order1 = new Order(new BigDecimal(100),"ShippingAddress 1",OrderStatus.UNPAID,user,cart1);

        //when
        userRepository.save(user);
        cartRepository.save(cart1);
        cartRepository.save(cart2);
        orderRepository.save(order1);

        //then
        Optional<User> testUser = userRepository.findById(user.getId());

        assertNotNull(testUser);
        assertEquals("New username", testUser.get().getUsername());
        assertEquals(1, testUser.get().getOrders().size());
        assertEquals(2, testUser.get().getCarts().size());
    }

    @Test
    void shouldUpdateUser() {
        //given
        User user = new User("New username","New password","New address");
        userRepository.save(user);

        Cart cart1 = new Cart();
        cart1.setUser(user);
        cartRepository.save(cart1);
        Cart cart2 = new Cart();
        cart2.setUser(user);
        cartRepository.save(cart2);
        Cart cart3 = new Cart();
        cart3.setUser(user);
        cartRepository.save(cart3);

        Order order1 = new Order(new BigDecimal(100),"ShippingAddress 1",OrderStatus.UNPAID,user,cart1);
        Order order2 = new Order(new BigDecimal(200),"ShippingAddress 2",OrderStatus.PAID,user,cart2);
        Order order3 = new Order(new BigDecimal(300),"ShippingAddress 3",OrderStatus.UNPAID,user,cart3);
        orderRepository.save(order1);
        orderRepository.save(order2);

        //when
        orderRepository.save(order3);
        user.setPassword("Updated password");
        userRepository.save(user);

        //then
        Optional<User> testUser = userRepository.findById(user.getId());
        assertEquals("Updated password", testUser.get().getPassword());
        assertEquals(3, testUser.get().getOrders().size());
    }
}