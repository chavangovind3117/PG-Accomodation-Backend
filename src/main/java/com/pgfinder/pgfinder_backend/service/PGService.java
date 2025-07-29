//src/main/java/com/pgfinder/service/PGService.java
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

	public List<PG> getAllPGs() {
		return pgRepository.findAll();
	}

	public Optional<PG> getPGById(Long id) {
		return pgRepository.findById(id);
	}

	public List<PG> getPGsByOwner(User owner) {
		return pgRepository.findByOwner(owner);
	}

	public List<PG> getPGsByCity(String city) {
		return pgRepository.findByCity(city);
	}

	public List<PG> getPGsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
		return pgRepository.findByPriceRange(minPrice, maxPrice);
	}

	public PG createPG(PG pg) {
		return pgRepository.save(pg);
	}

	public PG updatePG(PG pg) {
		return pgRepository.save(pg);
	}

	public void deletePG(Long id) {
		pgRepository.deleteById(id);
	}
}
