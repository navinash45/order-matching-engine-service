package com.order.engine.service;

import java.util.List;

import com.order.engine.dto.OrderDto;
import com.order.engine.dto.OrderRequest;

public interface OrderService {
	OrderDto addOrder(OrderRequest request);

	OrderDto getOrder(String orderId);

	List<OrderDto> getOrdersBySymbol(String symbol);
}
