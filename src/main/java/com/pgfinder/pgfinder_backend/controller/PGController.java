// src/main/java/com/pgfinder/pgfinder_backend/controller/PGController.java
package com.pgfinder.pgfinder_backend.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgfinder.pgfinder_backend.Enum.PGStatus;
import com.pgfinder.pgfinder_backend.entity.PG;
import com.pgfinder.pgfinder_backend.entity.User;
import com.pgfinder.pgfinder_backend.service.PGService;
import com.pgfinder.pgfinder_backend.service.UserService;

@RestController
@RequestMapping("/api/pgs")
@CrossOrigin(origins = "*")
public class PGController {

	@Autowired
	private PGService pgService;

	@Autowired
	private UserService userService;

	// Test endpoint
	@GetMapping("/test")
	public ResponseEntity<String> test() {
		return ResponseEntity.ok("PG API is working!");
	}

	// Get all PGs
	@GetMapping
	public ResponseEntity<?> getAllPGs() {
		try {
			List<PG> pgs = pgService.getAllPGs();
			System.out.println("Found " + pgs.size() + " PGs");
			for (PG pg : pgs) {
				System.out.println("PG: " + pg.getName() + " - " + pg.getLocation() + " - " + pg.getPrice());
			}
			return ResponseEntity.ok(pgs);
		} catch (Exception e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("message", "Failed to fetch PGs: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Get PG by ID
	@GetMapping("/{id}")
	public ResponseEntity<?> getPGById(@PathVariable Long id) {
		try {
			Optional<PG> pg = pgService.getPGById(id);
			if (pg.isPresent()) {
				return ResponseEntity.ok(pg.get());
			} else {
				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("message", "PG not found with ID: " + id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
			}
		} catch (Exception e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("message", "Failed to fetch PG: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Search PGs
	@GetMapping("/search")
	public ResponseEntity<?> searchPGs(@RequestParam(required = false) String city,
			@RequestParam(required = false) BigDecimal minPrice, @RequestParam(required = false) BigDecimal maxPrice) {
		try {
			List<PG> pgs;
			if (city != null && minPrice != null && maxPrice != null) {
				pgs = pgService.getPGsByPriceRange(minPrice, maxPrice);
			} else if (city != null) {
				pgs = pgService.getPGsByCity(city);
			} else {
				pgs = pgService.getAllPGs();
			}
			return ResponseEntity.ok(pgs);
		} catch (Exception e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("message", "Failed to search PGs: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Get PGs by owner ID
	@GetMapping("/owner/{ownerId}")
	public ResponseEntity<?> getPGsByOwner(@PathVariable Long ownerId) {
		try {
			List<PG> pgs = pgService.getPGsByOwnerId(ownerId);
			return ResponseEntity.ok(pgs);
		} catch (Exception e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("message", "Failed to fetch owner PGs: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Create new PG with images (Updated for single JSON)
	@PostMapping
	public ResponseEntity<?> createPG(@RequestBody Map<String, Object> requestBody) {

		try {
			// Extract data from request body
			ObjectMapper objectMapper = new ObjectMapper();

			// Extract pgData
			Map<String, Object> pgDataMap = (Map<String, Object>) requestBody.get("pgData");
			PG pg = objectMapper.convertValue(pgDataMap, PG.class);

			// Extract ownerId
			Long ownerId = Long.valueOf(requestBody.get("ownerId").toString());

			// Extract images (if any) - this would be base64 encoded strings
			List<String> imageUrls = new ArrayList<>();
			if (requestBody.containsKey("images")) {
				List<String> base64Images = (List<String>) requestBody.get("images");
				for (int i = 0; i < base64Images.size(); i++) {
					// Convert base64 to file and save
					String imageUrl = "/uploads/pg-images/" + System.currentTimeMillis() + "_" + i + ".jpg";
					// You would need to implement base64 to file conversion here
					imageUrls.add(imageUrl);
				}
				pg.setImages(imageUrls);
			}

			// Set the owner
			Optional<User> ownerOpt = userService.getUserById(ownerId);
			if (!ownerOpt.isPresent()) {
				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("message", "Owner not found with ID: " + ownerId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
			}
			pg.setOwner(ownerOpt.get());

			// Save the PG
			PG savedPG = pgService.createPG(pg);

			Map<String, Object> response = new HashMap<>();
			response.put("message", "PG created successfully");
			response.put("pg", savedPG);

			return ResponseEntity.status(HttpStatus.CREATED).body(response);

		} catch (Exception e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("message", "Failed to create PG: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Update PG
	@PutMapping("/{id}")
	public ResponseEntity<?> updatePG(@PathVariable Long id, @RequestBody PG pg) {
		try {
			Optional<PG> existingPG = pgService.getPGById(id);
			if (existingPG.isPresent()) {
				pg.setId(id);
				PG updatedPG = pgService.updatePG(pg);

				Map<String, Object> response = new HashMap<>();
				response.put("message", "PG updated successfully");
				response.put("pg", updatedPG);

				return ResponseEntity.ok(response);
			} else {
				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("message", "PG not found with ID: " + id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
			}
		} catch (Exception e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("message", "Failed to update PG: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Partial update PG
	@PatchMapping("/{id}")
	public ResponseEntity<?> partialUpdatePG(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
		try {
			Optional<PG> existingPGOpt = pgService.getPGById(id);

			if (existingPGOpt.isPresent()) {
				PG existingPG = existingPGOpt.get();

				// Update fields based on the map
				updates.forEach((key, value) -> {
					switch (key) {
					case "name":
						existingPG.setName((String) value);
						break;
					case "location":
						existingPG.setLocation((String) value);
						break;
					case "description":
						existingPG.setDescription((String) value);
						break;
					case "price":
						if (value instanceof Number) {
							existingPG.setPrice(BigDecimal.valueOf(((Number) value).doubleValue()));
						}
						break;
					case "address":
						existingPG.setAddress((String) value);
						break;
					case "city":
						existingPG.setCity((String) value);
						break;
					case "state":
						existingPG.setState((String) value);
						break;
					case "amenities":
						if (value instanceof List) {
							existingPG.setAmenities((List<String>) value);
						}
						break;
					case "rating":
						if (value instanceof Number) {
							existingPG.setRating(((Number) value).doubleValue());
						}
						break;
					case "reviewCount":
						if (value instanceof Number) {
							existingPG.setReviewCount(((Number) value).intValue());
						}
						break;
					case "status":
						existingPG.setStatus(PGStatus.valueOf((String) value));
						break;
					}
				});

				PG updatedPG = pgService.updatePG(existingPG);

				Map<String, Object> response = new HashMap<>();
				response.put("message", "PG updated successfully");
				response.put("pg", updatedPG);

				return ResponseEntity.ok(response);
			} else {
				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("message", "PG not found with ID: " + id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
			}
		} catch (Exception e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("message", "Failed to update PG: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Delete PG
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletePG(@PathVariable Long id) {
		try {
			Optional<PG> pg = pgService.getPGById(id);
			if (pg.isPresent()) {
				pgService.deletePG(id);

				Map<String, String> response = new HashMap<>();
				response.put("message", "PG deleted successfully");

				return ResponseEntity.ok(response);
			} else {
				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("message", "PG not found with ID: " + id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
			}
		} catch (Exception e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("message", "Failed to delete PG: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Upload images for existing PG
	@PostMapping("/{id}/images")
	public ResponseEntity<?> uploadImages(@PathVariable Long id, @RequestParam("images") MultipartFile[] images) {
		try {
			Optional<PG> pgOpt = pgService.getPGById(id);
			if (!pgOpt.isPresent()) {
				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("message", "PG not found with ID: " + id);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
			}

			PG pg = pgOpt.get();

			// Handle image uploads
			if (images != null && images.length > 0) {
				List<String> imageUrls = new ArrayList<>();
				for (int i = 0; i < images.length; i++) {
					imageUrls.add("/uploads/pg-images/" + System.currentTimeMillis() + "_" + i + ".jpg");
				}
				pg.setImages(imageUrls);
				pgService.updatePG(pg);
			}

			Map<String, Object> response = new HashMap<>();
			response.put("message", "Images uploaded successfully");
			response.put("pg", pg);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("message", "Failed to upload images: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
}