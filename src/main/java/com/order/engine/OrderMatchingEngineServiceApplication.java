package com.order.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;

@SpringBootApplication
@Cacheable
public class OrderMatchingEngineServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderMatchingEngineServiceApplication.class, args);
	}

}
