//src/main/java/com/pgfinder/controller/PGController.java
package com.pgfinder.pgfinder_backend.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.pgfinder.pgfinder_backend.entity.PG;
import com.pgfinder.pgfinder_backend.service.PGService;

@RestController
@RequestMapping("/api/pgs")
@CrossOrigin(origins = "*")
public class PGController {

	@Autowired
	private PGService pgService;

	@GetMapping
	public List<PG> getAllPGs() {
		List<PG> pgs = pgService.getAllPGs();
		System.out.println("Found " + pgs.size() + " PGs");
		for (PG pg : pgs) {
			System.out.println("PG: " + pg.getName() + " - " + pg.getLocation() + " - " + pg.getPrice());
		}
		return pgs;
	}

	@GetMapping("/{id}")
	public ResponseEntity<PG> getPGById(@PathVariable Long id) {
		Optional<PG> pg = pgService.getPGById(id);
		return pg.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/search")
	public List<PG> searchPGs(@RequestParam(required = false) String city,
			@RequestParam(required = false) BigDecimal minPrice, @RequestParam(required = false) BigDecimal maxPrice) {
		if (city != null && minPrice != null && maxPrice != null) {
			return pgService.getPGsByPriceRange(minPrice, maxPrice);
		} else if (city != null) {
			return pgService.getPGsByCity(city);
		}
		return pgService.getAllPGs();
	}

	@PostMapping
	public PG createPG(@RequestBody PG pg) {
		return pgService.createPG(pg);
	}

	@PutMapping("/{id}")
	public ResponseEntity<PG> updatePG(@PathVariable Long id, @RequestBody PG pg) {
		Optional<PG> existingPG = pgService.getPGById(id);
		if (existingPG.isPresent()) {
			pg.setId(id);
			return ResponseEntity.ok(pgService.updatePG(pg));
		}
		return ResponseEntity.notFound().build();
	}

	// Add this method to your PGController.java
	@PatchMapping("/{id}")
	public ResponseEntity<PG> partialUpdatePG(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
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
				}
			});

			PG updatedPG = pgService.updatePG(existingPG);
			return ResponseEntity.ok(updatedPG);
		}

		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePG(@PathVariable Long id) {
		Optional<PG> pg = pgService.getPGById(id);
		if (pg.isPresent()) {
			pgService.deletePG(id);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}
}
