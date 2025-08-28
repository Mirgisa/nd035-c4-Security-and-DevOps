package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static net.logstash.logback.argument.StructuredArguments.kv;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/user")
public class UserController {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserController.class);

	private UserRepository userRepository;
	private CartRepository cartRepository;
	private PasswordEncoder passwordEncoder;

	@Autowired
	public UserController(UserRepository userRepository, CartRepository cartRepository,
						  PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.cartRepository = cartRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public UserController() {

	}

	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest req) {
		log.info("CreateUser attempt {}", kv("event","CreateUser"), kv("username", req.getUsername()));

		if (req.getPassword().length() < 8 ||
				!req.getPassword().equals(req.getConfirmPassword())) {
			log.warn("CreateUser failure {}",
					kv("event","CreateUser"),
					kv("username", req.getUsername()),
					kv("outcome","failure"),
					kv("reason","password_policy"));
			return ResponseEntity.badRequest().build();
		}

		User user = new User();
		user.setUsername(req.getUsername());
		user.setPassword(passwordEncoder.encode(req.getPassword()));

		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		cart.setUser(user);

		userRepository.save(user);

		log.info("CreateUser success {}",
				kv("event","CreateUser"),
				kv("username", user.getUsername()),
				kv("outcome","success"),
				kv("status", 201));

		return ResponseEntity.status(HttpStatus.CREATED).body(user);
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		Optional<User> userOpt = userRepository.findById(id);
		return userOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/username/{username}")
	public ResponseEntity<User> findByUsername(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
}
