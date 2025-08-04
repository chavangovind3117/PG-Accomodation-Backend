// src/main/java/com/pgfinder/pgfinder_backend/security/SimpleJwtUtil.java
package com.pgfinder.pgfinder_backend.security;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class SimpleJwtUtil {
	private final String secret = "your-secret-key-here";
	private final long expirationMs = 86400000; // 1 day

	public String generateToken(String email, String role, Long userId) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("email", email);
		claims.put("role", role);
		claims.put("userId", userId);
		claims.put("exp", new Date(System.currentTimeMillis() + expirationMs));
		claims.put("iat", new Date());

		// Simple encoding (for production, use proper JWT library)
		String payload = email + ":" + role + ":" + userId + ":" + claims.get("exp");
		return Base64.getEncoder().encodeToString(payload.getBytes());
	}

	public String extractEmail(String token) {
		try {
			String decoded = new String(Base64.getDecoder().decode(token));
			return decoded.split(":")[0];
		} catch (Exception e) {
			return null;
		}
	}

	public boolean validateToken(String token, String email) {
		try {
			String extractedEmail = extractEmail(token);
			return email.equals(extractedEmail);
		} catch (Exception e) {
			return false;
		}
	}
}