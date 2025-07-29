//src/main/java/com/pgfinder/repository/BookingRepository.java
package com.pgfinder.pgfinder_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pgfinder.pgfinder_backend.entity.Booking;
import com.pgfinder.pgfinder_backend.entity.PG;
import com.pgfinder.pgfinder_backend.entity.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
 List<Booking> findByUser(User user);
 List<Booking> findByPg(PG pg);
 List<Booking> findByPgOwner(User owner);
}
