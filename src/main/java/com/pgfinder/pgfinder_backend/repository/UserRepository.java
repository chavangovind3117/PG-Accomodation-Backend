//src/main/java/com/pgfinder/repository/UserRepository.java
package com.pgfinder.pgfinder_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pgfinder.pgfinder_backend.Enum.UserRole;
import com.pgfinder.pgfinder_backend.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
 Optional<User> findByEmail(String email);
 List<User> findByRole(UserRole role);
 boolean existsByEmail(String email);
}