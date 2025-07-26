package com.order.engine.util;

import com.order.engine.dto.OrderDto;
import com.order.engine.entity.Order;
import com.order.engine.entity.OrderEntity;

public class OrderMapper {

	public static OrderDto convertToDTO(Order order) {
		return OrderDto.builder().id(order.getId()).price(order.getPrice()).quantity(order.getQuantity())
				.type(order.getOrderType()).symbol(order.getSymbol()).timestamp(order.getTimestamp())
				.status(order.getStatus()).build();
		//.remainingQuantity(order.getRemainingQuantity())
	}

	public static OrderEntity mapToEntity(Order order) {
		OrderEntity entity = OrderEntity.builder().id(order.getId()).symbol(order.getSymbol()).price(order.getPrice())
				.quantity(order.getQuantity()).orderType(order.getOrderType()).timestamp(order.getTimestamp())
				.status(order.getStatus()).remainingQuantity(order.getQuantity()).isCompleted(false).build();
		return entity;
	}

	public static OrderDto mapToDto(OrderEntity e) {
		return OrderDto.builder().id(e.getId()).symbol(e.getSymbol()).price(e.getPrice()).quantity(e.getQuantity())
				.type(e.getOrderType()).timestamp(e.getTimestamp()).status(e.getStatus())
				.build();
		//.remainingQuantity(e.getRemainingQuantity())
	}
}
