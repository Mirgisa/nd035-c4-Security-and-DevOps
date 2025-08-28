package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class OrderControllerTest {
    private OrderController orderController;
    private OrderRepository orderRepo = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setUp(){
        orderController  = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
    }

    @Test
    public void submitOrderTest() {
        String username = "testuser";

        // Create user
        User user = new User();
        user.setUsername(username);
        user.setId(1L);

        // Create cart with an item
        Item item = new Item();
        item.setId(1L);
        item.setName("Laptop");
        item.setPrice(BigDecimal.valueOf(999.99));

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(Collections.singletonList(item));
        cart.setTotal(item.getPrice());

        user.setCart(cart);

        // Mock repository call
        when(userRepository.findByUsername(username)).thenReturn(user);

        // Call controller
        ResponseEntity<UserOrder> response = orderController.submit(username);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(username, order.getUser().getUsername());
        assertEquals(1, order.getItems().size());
        assertEquals(item.getPrice(), order.getTotal());

        // Verify that save was called once
        verify(orderRepo, times(1)).save(any(UserOrder.class));
    }
    @Test
    public void getOrdersForUserTest() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setId(1L);
        user.setCart(new Cart());
        when(orderRepo.findByUser(user)).thenReturn(Collections.singletonList(new UserOrder()));
        assertNotNull(orderController.getOrdersForUser(username));

    }
}
