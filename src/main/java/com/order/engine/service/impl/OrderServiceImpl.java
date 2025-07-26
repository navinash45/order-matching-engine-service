package com.order.engine.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.order.engine.constants.OrderStatus;
import com.order.engine.dto.OrderDto;
import com.order.engine.dto.OrderRequest;
import com.order.engine.entity.Order;
import com.order.engine.repository.OrderRepository;
import com.order.engine.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;

	public OrderServiceImpl(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	public OrderDto addOrder(OrderRequest request) {
		Order order = Order.builder().id(UUID.randomUUID().toString()).symbol(request.getSymbol())
				.orderType(request.getType().name()).price(request.getPrice()).quantity(request.getQuantity())
				.timestamp(System.currentTimeMillis()).status(OrderStatus.NEW.name())
				.remainingQuantity(request.getQuantity()).build();
		return orderRepository.addOrder(order);
	}

	@Override
	public OrderDto getOrder(String orderId) {
		return orderRepository.getOrderById(orderId);

	}

	@Override
	public List<OrderDto> getOrdersBySymbol(String symbol) {
		return orderRepository.getOrdersBySymbol(symbol);
	}

}