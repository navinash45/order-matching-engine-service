package com.order.engine.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class OrderRepositoryHealthIndicator implements HealthIndicator {

	@Override
	public Health health() {
		return Health.up().withDetail("status", "In-memory Order Repository is healthy")
				.withDetail("heapUsed", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()).build();
	}
}