// src/main/java/com/pgfinder/pgfinder_backend/config/CorsConfig.java
package com.pgfinder.pgfinder_backend.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();

		// Set allowCredentials to false OR use specific origins
		config.setAllowCredentials(false); // Changed from true to false

		// Use allowedOriginPatterns instead of allowedOrigins with "*"
		config.setAllowedOriginPatterns(Arrays.asList("*")); // This allows all origins

		// OR if you want to allow specific origins with credentials:
		// config.setAllowCredentials(true);
		// config.setAllowedOrigins(Arrays.asList("http://localhost:5173",
		// "http://127.0.0.1:5173"));

		config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization"));
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}