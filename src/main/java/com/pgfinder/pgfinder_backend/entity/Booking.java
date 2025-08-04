//src/main/java/com/pgfinder/entity/Booking.java
package com.pgfinder.pgfinder_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.pgfinder.pgfinder_backend.Enum.BookingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "pg_id", nullable = false)
	private PG pg;

	@Column(name = "check_in_date", nullable = false)
	private LocalDate checkInDate;

	@Column(name = "duration_months")
	private Integer durationMonths;

	@Column(name = "total_amount")
	private BigDecimal totalAmount;

	@Enumerated(EnumType.STRING)
	private BookingStatus status = BookingStatus.PENDING;

	@Column(name = "special_requirements", columnDefinition = "TEXT")
	private String specialRequirements;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
