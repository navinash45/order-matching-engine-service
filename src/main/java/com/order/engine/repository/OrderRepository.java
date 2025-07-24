package com.order.engine.repository;

import java.util.List;

import com.order.engine.dto.OrderDto;
import com.order.engine.entity.Order;

public interface OrderRepository {
	OrderDto addOrder(Order order);

	OrderDto getOrderById(String id);

	List<OrderDto> getOrdersBySymbol(String symbol);

}
