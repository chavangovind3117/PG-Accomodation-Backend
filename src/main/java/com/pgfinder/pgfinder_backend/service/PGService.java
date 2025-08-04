// src/main/java/com/pgfinder/pgfinder_backend/service/PGService.java
package com.pgfinder.pgfinder_backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pgfinder.pgfinder_backend.entity.PG;
import com.pgfinder.pgfinder_backend.entity.User;
import com.pgfinder.pgfinder_backend.repository.PGRepository;

@Service
public class PGService {

	@Autowired
	private PGRepository pgRepository;

	// Get all PGs
	public List<PG> getAllPGs() {
		return pgRepository.findAll();
	}

	// Get PG by ID
	public Optional<PG> getPGById(Long id) {
		return pgRepository.findById(id);
	}

	// Get PGs by owner
	public List<PG> getPGsByOwner(User owner) {
		return pgRepository.findByOwner(owner);
	}

	// Get PGs by owner ID
	public List<PG> getPGsByOwnerId(Long ownerId) {
		return pgRepository.findByOwnerId(ownerId);
	}

	// Get PGs by city
	public List<PG> getPGsByCity(String city) {
		return pgRepository.findByCityIgnoreCase(city);
	}

	// Get PGs by price range
	public List<PG> getPGsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
		return pgRepository.findByPriceBetween(minPrice, maxPrice);
	}

	// Create new PG
	public PG createPG(PG pg) {
		return pgRepository.save(pg);
	}

	// Update PG
	public PG updatePG(PG pg) {
		return pgRepository.save(pg);
	}

	// Delete PG
	public void deletePG(Long id) {
		pgRepository.deleteById(id);
	}
}