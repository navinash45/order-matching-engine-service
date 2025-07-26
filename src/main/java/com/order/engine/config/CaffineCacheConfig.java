package com.order.engine.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class CaffineCacheConfig {
	@Bean
	public CacheManager cacheManager() {
		Caffeine<Object, Object> caffeine = Caffeine.newBuilder().maximumSize(1000).expireAfterWrite(10,
				TimeUnit.MINUTES);
		CaffeineCacheManager cacheManager = new CaffeineCacheManager("ordersById", "ordersBySymbol");
		cacheManager.setCaffeine(caffeine);
		return cacheManager;
	}
}