package com.order.engine.service.impl;

import com.order.engine.constants.OrderStatus;
import com.order.engine.constants.OrderType;
import com.order.engine.dto.OrderDto;
import com.order.engine.dto.OrderRequest;
import com.order.engine.entity.Order;
import com.order.engine.repository.OrderRepository;
import com.order.enginer.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceImplTest {

	private OrderRepository orderRepository;
	private OrderService orderService;

	@BeforeEach
	void setUp() {
		orderRepository = mock(OrderRepository.class);
		orderService = new OrderServiceImpl(orderRepository);
	}

	@Test
	void testAddOrder() {
		OrderRequest request = new OrderRequest();
		request.setSymbol("AAPL");
		request.setPrice(150.0);
		request.setQuantity(10);
		request.setType(OrderType.BUY);

		ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

		orderService.addOrder(request);

		verify(orderRepository).addOrder(captor.capture());

		Order captured = captor.getValue();
		assertEquals("AAPL", captured.getSymbol());
		assertEquals("BUY", captured.getOrderType());
		assertEquals(150.0, captured.getPrice());
		assertEquals(10, captured.getQuantity());
		assertEquals(OrderStatus.NEW.name(), captured.getStatus());
		assertNotNull(captured.getId());
		assertNotNull(captured.getTimestamp());
	}

	@Test
	void testGetOrder() {
		String orderId = UUID.randomUUID().toString();
		OrderDto expectedDto = new OrderDto();
		when(orderRepository.getOrderById(orderId)).thenReturn(expectedDto);

		OrderDto actual = orderService.getOrder(orderId);

		assertEquals(expectedDto, actual);
		verify(orderRepository).getOrderById(orderId);
	}

	@Test
	void testGetOrdersBySymbol() {
		String symbol = "AAPL";
		List<OrderDto> expected = List.of(new OrderDto(), new OrderDto());
		when(orderRepository.getOrdersBySymbol(symbol)).thenReturn(expected);

		List<OrderDto> actual = orderService.getOrdersBySymbol(symbol);

		assertEquals(expected, actual);
		verify(orderRepository).getOrdersBySymbol(symbol);
	}
}
