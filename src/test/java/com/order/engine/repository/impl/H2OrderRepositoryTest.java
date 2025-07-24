package com.order.engine.repository.impl;

import com.order.engine.constants.OrderStatus;
import com.order.engine.dto.OrderDto;
import com.order.engine.entity.Order;
import com.order.engine.entity.OrderEntity;
import com.order.engine.exception.OrderNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class H2OrderRepositoryTest {

	private H2JPAOrderRepository jpaRepo;
	private H2OrderRepository repository;

	@BeforeEach
	void setUp() {
		jpaRepo = mock(H2JPAOrderRepository.class);
		repository = new H2OrderRepository(jpaRepo);
	}

	@Test
	void testAddOrder_MatchAndSaveStatus() {
		Order order = createOrder("AAPL", "BUY", 100.0, 10);
		OrderEntity orderEntity = createOrderEntity(order);
		OrderEntity savedEntity = clone(orderEntity);

		when(jpaRepo.save(any(OrderEntity.class))).thenReturn(savedEntity);
		when(jpaRepo.findBySymbolAndOrderTypeAndIsCompletedOrderByTimestampAsc("AAPL", "SELL", false))
				.thenReturn(Collections.emptyList());

		repository.addOrder(order);

		verify(jpaRepo, atLeastOnce()).save(any(OrderEntity.class));
	}

	@Test
	void testGetOrdersBySymbol_Success() {
		OrderEntity orderEntity = createOrderEntity(createOrder("AAPL", "SELL", 200.0, 5));
		when(jpaRepo.findBySymbolAndIsCompletedFalse("AAPL")).thenReturn(List.of(orderEntity));

		List<OrderDto> result = repository.getOrdersBySymbol("AAPL");

		assertEquals(1, result.size());
		assertEquals("AAPL", result.get(0).getSymbol());
	}

	@Test
	void testGetOrdersBySymbol_NotFound() {
		when(jpaRepo.findBySymbolAndIsCompletedFalse("XYZ")).thenReturn(Collections.emptyList());
		assertThrows(OrderNotFoundException.class, () -> repository.getOrdersBySymbol("XYZ"));
	}

	@Test
	void testGetOrderById_Success() {
		OrderEntity entity = createOrderEntity(createOrder("GOOG", "BUY", 1200.0, 3));
		when(jpaRepo.findById(entity.getId())).thenReturn(Optional.of(entity));

		OrderDto dto = repository.getOrderById(entity.getId());
		assertEquals("GOOG", dto.getSymbol());
	}

	@Test
	void testGetOrderById_NotFound() {
		when(jpaRepo.findById("not-found")).thenReturn(Optional.empty());
		assertThrows(OrderNotFoundException.class, () -> repository.getOrderById("not-found"));
	}

	private Order createOrder(String symbol, String type, double price, int qty) {
		return Order.builder().id(UUID.randomUUID().toString()).symbol(symbol).orderType(type).price(price)
				.quantity(qty).timestamp(System.currentTimeMillis()).status(OrderStatus.NONE.name()).build();
	}

	private OrderEntity createOrderEntity(Order o) {
		return OrderEntity.builder().id(o.getId()).symbol(o.getSymbol()).price(o.getPrice()).quantity(o.getQuantity())
				.orderType(o.getOrderType()).timestamp(o.getTimestamp()).status(o.getStatus())
				.remainingQuantity(o.getQuantity()).isCompleted(false).build();
	}

	private OrderEntity clone(OrderEntity o) {
		return OrderEntity.builder().id(o.getId()).symbol(o.getSymbol()).price(o.getPrice()).quantity(o.getQuantity())
				.orderType(o.getOrderType()).timestamp(o.getTimestamp()).status(o.getStatus())
				.remainingQuantity(o.getRemainingQuantity()).isCompleted(o.isCompleted()).build();
	}
}
