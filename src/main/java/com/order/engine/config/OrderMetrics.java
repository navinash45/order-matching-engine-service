package com.order.engine.config;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;

@Component
public class OrderMetrics {
	private final MeterRegistry registry;

	public OrderMetrics(MeterRegistry registry) {
		this.registry = registry;
	}

	public void recordOrder(String type) {
		registry.counter("orders.total", "type", type).increment();
	}

	public void recordMatch() {
		registry.counter("orders.matched").increment();
	}
}
