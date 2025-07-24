package com.order.engine.repository.impl;

import com.order.engine.dto.OrderDto;
import com.order.engine.entity.Order;
import com.order.engine.exception.OrderNotFoundException;
import com.order.engine.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JavaCollectionOrderRepositoryTest {

	private OrderRepository repository;

	@BeforeEach
	void setUp() {
		repository = new JavaCollectionOrderRepository();
	}

	@Test
	void testAddBuyOrder_NoMatch_StatusNone() {
		Order order = createOrder("AAPL", "BUY", 150.0, 10);
		repository.addOrder(order);

		OrderDto fetched = repository.getOrderById(order.getId());
		assertEquals("NONE", fetched.getStatus());
		assertEquals(10, fetched.getQuantity());
	}

//	@Test
//	void testAddSellOrder_MatchesBuyOrder_StatusCompleted() {
//		Order buyOrder = createOrder("AAPL", "BUY", 150.0, 10);
//		repository.addOrder(buyOrder);
//
//		Order sellOrder = createOrder("AAPL", "SELL", 140.0, 10);
//		repository.addOrder(sellOrder);
//
//		OrderDto buyFetched = repository.getOrderById(buyOrder.getId());
//		OrderDto sellFetched = repository.getOrderById(sellOrder.getId());
//
//		assertEquals("COMPLETED", buyFetched.getStatus());
//		assertEquals("COMPLETED", sellFetched.getStatus());
//	}

	@Test
	void testPartialMatch_StatusPartial() {
		Order buyOrder = createOrder("AAPL", "BUY", 150.0, 5);
		repository.addOrder(buyOrder);

		Order sellOrder = createOrder("AAPL", "SELL", 145.0, 10);
		repository.addOrder(sellOrder);

		OrderDto sellFetched = repository.getOrderById(sellOrder.getId());

		assertEquals("PARTIAL", sellFetched.getStatus());
		assertEquals(5, sellFetched.getQuantity());
	}

	@Test
	void testGetOrdersBySymbol() {
		Order order1 = createOrder("TSLA", "BUY", 300.0, 5);
		Order order2 = createOrder("TSLA", "SELL", 310.0, 5);

		repository.addOrder(order1);
		repository.addOrder(order2);

		List<OrderDto> orders = repository.getOrdersBySymbol("TSLA");

		assertEquals(2, orders.size());
		assertTrue(orders.stream().anyMatch(o -> o.getId().equals(order1.getId())));
		assertTrue(orders.stream().anyMatch(o -> o.getId().equals(order2.getId())));
	}

	@Test
	void testGetOrderById_NotFound_ThrowsException() {
		assertThrows(OrderNotFoundException.class, () -> repository.getOrderById("non-existent-id"));
	}

	@Test
	void testGetOrdersBySymbol_NotFound_ThrowsException() {
		assertThrows(OrderNotFoundException.class, () -> repository.getOrdersBySymbol("XYZ"));
	}

	private Order createOrder(String symbol, String type, double price, int qty) {
		return Order.builder().id(UUID.randomUUID().toString()).symbol(symbol).orderType(type).price(price)
				.quantity(qty).timestamp(System.currentTimeMillis()).build();
	}
}
