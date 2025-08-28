package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

import static net.logstash.logback.argument.StructuredArguments.kv;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderController.class);
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;


	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		log.info("OrderSubmit attempt {}", kv("event","OrderSubmit"), kv("username", username));

		User user = userRepository.findByUsername(username);
		if (user == null) {
			log.warn("OrderSubmit failure {}",
					kv("event","OrderSubmit"),
					kv("username", username),
					kv("outcome","failure"),
					kv("reason","user_not_found"),
					kv("status", 404));
			return ResponseEntity.notFound().build();
		}

		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);

		log.info("OrderSubmit success {}",
				kv("event","OrderSubmit"),
				kv("username", username),
				kv("orderId", order.getId()),
				kv("itemCount", order.getItems().size()),
				kv("total", order.getTotal()),
				kv("outcome","success"),
				kv("status", 200));

		return ResponseEntity.ok(order);
	}


	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		List<UserOrder> orders = orderRepository.findByUser(user);
		return ResponseEntity.ok(orders);
	}
	@GetMapping("/purchase-history")
	public ResponseEntity<String> getPurchaseHistory() {

		return ResponseEntity.ok("Your purchase history (protected)");
	}
}
