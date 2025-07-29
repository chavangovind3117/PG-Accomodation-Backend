//src/main/java/com/pgfinder/repository/PGRepository.java
package com.pgfinder.pgfinder_backend.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pgfinder.pgfinder_backend.Enum.PGStatus;
import com.pgfinder.pgfinder_backend.entity.PG;
import com.pgfinder.pgfinder_backend.entity.User;

@Repository
public interface PGRepository extends JpaRepository<PG, Long> {
	List<PG> findByOwner(User owner);

	List<PG> findByCity(String city);

	List<PG> findByStatus(PGStatus status);

	@Query("SELECT p FROM PG p WHERE p.price BETWEEN ?1 AND ?2")
	List<PG> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
}
