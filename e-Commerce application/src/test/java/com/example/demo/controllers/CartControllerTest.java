package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CartControllerTest {
    private CartController cartController;
    private CartRepository cartRepo = mock(CartRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);
    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        cartController  = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);


        // Mock SecurityContext with authenticated user
        SecurityContext securityContext = mock(SecurityContext.class);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("mirgisa", null, new ArrayList<>());
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void addToCartTest() {
        User user = new User();
        user.setUsername("mirgisa");
        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);

        Item item = new Item();
        item.setId(1L);
        item.setName("Sample Item");
        item.setPrice(new BigDecimal("10.0"));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("mirgisa");
        request.setItemId(1L);
        request.setQuantity(2);

        // Mock repository behavior
        when(userRepo.findByUsername("mirgisa")).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepo.save(any(Cart.class))).thenReturn(cart);

        // Call method
        ResponseEntity<Cart> response = cartController.addTocart(request);

        // Verify
        assertNotNull(response);
        assertEquals(2, cart.getItems().size());
        assertEquals(new BigDecimal("20.0"), cart.getTotal());
        verify(cartRepo, times(1)).save(cart);
    }
    @Test
    public void removeFromCartTest() {
        // Setup user, cart, item
        User user = new User();
        user.setUsername("mirgisa");
        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);

        Item item = new Item();
        item.setId(1L);
        item.setName("Sample Item");
        item.setPrice(new BigDecimal("10.0"));

        cart.addItem(item); // add one item

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);

        // Mock repository behavior
        when(userRepo.findByUsername("mirgisa")).thenReturn(user);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepo.save(any(Cart.class))).thenReturn(cart);

        // Call controller
        ResponseEntity<Cart> response = cartController.removeFromCart(request);

        // Verify
        assertNotNull(response);
        assertEquals(0, cart.getItems().size());
        assertEquals(new BigDecimal("0.0"), cart.getTotal());
        verify(cartRepo, times(1)).save(cart);
    }
    @Test
    public void getCartDetailsTest() {
        User user = new User();
        user.setUsername("mirgisa");
        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);

        // Mock repository behavior
        when(userRepo.findByUsername("mirgisa")).thenReturn(user);

        // Call method
        ResponseEntity<Cart> response = cartController.getCart();

        // Verify
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(cart, response.getBody());
    }
}
