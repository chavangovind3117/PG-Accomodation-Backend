// src/main/java/com/pgfinder/pgfinder_backend/repository/PGRepository.java
package com.pgfinder.pgfinder_backend.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pgfinder.pgfinder_backend.entity.PG;
import com.pgfinder.pgfinder_backend.entity.User;

@Repository
public interface PGRepository extends JpaRepository<PG, Long> {

	// Basic methods
	List<PG> findByOwner(User owner);

	// Find by owner ID using custom query
	@Query("SELECT p FROM PG p WHERE p.owner.id = :ownerId")
	List<PG> findByOwnerId(@Param("ownerId") Long ownerId);

	// Find by city (case-insensitive)
	@Query("SELECT p FROM PG p WHERE LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%'))")
	List<PG> findByCityIgnoreCase(@Param("city") String city);

	// Find by price range
	@Query("SELECT p FROM PG p WHERE p.price BETWEEN :minPrice AND :maxPrice")
	List<PG> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

	// Find by city and price range
	@Query("SELECT p FROM PG p WHERE LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%')) AND p.price BETWEEN :minPrice AND :maxPrice")
	List<PG> findByCityIgnoreCaseAndPriceBetween(@Param("city") String city, @Param("minPrice") BigDecimal minPrice,
			@Param("maxPrice") BigDecimal maxPrice);

	// Find by status
	@Query("SELECT p FROM PG p WHERE p.status = :status")
	List<PG> findByStatus(@Param("status") String status);

	// Find by minimum rating
	@Query("SELECT p FROM PG p WHERE p.rating >= :minRating")
	List<PG> findByRatingGreaterThanEqual(@Param("minRating") Double minRating);

	// Count by owner ID
	@Query("SELECT COUNT(p) FROM PG p WHERE p.owner.id = :ownerId")
	Long countByOwnerId(@Param("ownerId") Long ownerId);

	// Check existence by name and location
	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM PG p WHERE p.name = :name AND p.location = :location")
	boolean existsByNameAndLocation(@Param("name") String name, @Param("location") String location);
}