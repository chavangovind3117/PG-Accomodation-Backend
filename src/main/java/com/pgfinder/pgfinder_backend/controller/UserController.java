//src/main/java/com/pgfinder/controller/UserController.java
package com.pgfinder.pgfinder_backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pgfinder.pgfinder_backend.Enum.UserRole;
import com.pgfinder.pgfinder_backend.entity.User;
import com.pgfinder.pgfinder_backend.security.SimpleJwtUtil;
import com.pgfinder.pgfinder_backend.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Your React app URL
public class UserController {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@GetMapping("/test-db")
	public String testDatabase() {
		try {
			String result = jdbcTemplate.queryForObject("SELECT 'Database connected successfully!'", String.class);
			return result;
		} catch (Exception e) {
			return "Database connection failed: " + e.getMessage();
		}
	}

	@Autowired
	private UserService userService;

	@GetMapping
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id) {
		Optional<User> user = userService.getUserById(id);
		return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/signup")
	public User createUser(@RequestBody User user) {
		return userService.createUser(user);
	}

	// In your UserController.java
	@Autowired
	private SimpleJwtUtil jwtUtil; // Add this line

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
		try {
			String email = loginRequest.get("email");
			String password = loginRequest.get("password");
			String requestedRole = loginRequest.get("role");

			Optional<User> userOpt = userService.getUserByEmail(email);
			if (userOpt.isPresent()) {
				User user = userOpt.get();

				// Check if password matches
				if (user.getPassword().equals(password)) {

					// Validate that requested role matches user's actual role
					if (requestedRole != null && !user.getRole().toString().equals(requestedRole)) {
						Map<String, String> errorResponse = new HashMap<>();
						errorResponse.put("message",
								"Invalid role for this user. Expected: " + user.getRole() + ", Got: " + requestedRole);
						return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
					}

					// Generate JWT token
					String token = jwtUtil.generateToken(user.getEmail(), user.getRole().toString(), user.getId());

					// Create response with token, user, and success message
					Map<String, Object> response = new HashMap<>();
					response.put("message", "Login successful");
					response.put("token", token);
					response.put("user", user);

					return ResponseEntity.ok(response);
				} else {
					Map<String, String> errorResponse = new HashMap<>();
					errorResponse.put("message", "Invalid password");
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
				}
			} else {
				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("message", "User not found");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
			}
		} catch (Exception e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("message", "Login failed: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

// @PutMapping("/{id}")
// public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
//     Optional<User> existingUser = userService.getUserById(id);
//     if (existingUser.isPresent()) {
//         user.setId(id);
//         return ResponseEntity.ok(userService.updateUser(user));
//     }
//     return ResponseEntity.notFound().build();
// }

	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
		System.out.println("Received ID: " + id);
		System.out.println("Received User: " + user);

		try {
			Optional<User> existingUser = userService.getUserById(id);
			System.out.println("Existing user found: " + existingUser.isPresent());

			if (existingUser.isPresent()) {
				user.setId(id);
				User updatedUser = userService.updateUser(user);
				System.out.println("Updated user: " + updatedUser);
				return ResponseEntity.ok(updatedUser);
			}
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			System.out.println("General Exception: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	// Add this method to your UserController.java
	@PatchMapping("/{id}")
	public ResponseEntity<User> partialUpdateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
		System.out.println("Received PATCH request for user ID: " + id);
		System.out.println("Updates: " + updates);

		try {
			Optional<User> existingUserOpt = userService.getUserById(id);

			if (existingUserOpt.isPresent()) {
				User existingUser = existingUserOpt.get();

				// Update fields based on the map
				updates.forEach((key, value) -> {
					switch (key) {
					case "name":
						if (value != null) {
							existingUser.setName((String) value);
						}
						break;
					case "email":
						if (value != null) {
							existingUser.setEmail((String) value);
						}
						break;
					case "password":
						if (value != null) {
							existingUser.setPassword((String) value);
						}
						break;
					case "phone":
						if (value != null) {
							existingUser.setPhone((String) value);
						}
						break;
					case "occupation":
						if (value != null) {
							existingUser.setOccupation((String) value);
						}
						break;
					case "role":
						if (value != null) {
							existingUser.setRole(UserRole.valueOf((String) value));
						}
						break;
					default:
						System.out.println("Unknown field: " + key);
						break;
					}
				});

				User updatedUser = userService.updateUser(existingUser);
				System.out.println("Updated user: " + updatedUser);
				return ResponseEntity.ok(updatedUser);
			}

			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			System.out.println("PATCH Exception: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		Optional<User> user = userService.getUserById(id);
		if (user.isPresent()) {
			userService.deleteUser(id);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}
}
