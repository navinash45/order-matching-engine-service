package com.order.engine.repository.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.order.engine.constants.OrderStatus;
import com.order.engine.constants.OrderType;
import com.order.engine.dto.OrderDto;
import com.order.engine.entity.Order;
import com.order.engine.entity.OrderEntity;
import com.order.engine.exception.OrderNotFoundException;
import com.order.engine.repository.OrderRepository;
import com.order.enginer.util.OrderMapper;

@Repository
@Profile("!collection")
public class H2OrderRepository implements OrderRepository {

	private final H2JPAOrderRepository orderRepository;

	public H2OrderRepository(H2JPAOrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	public OrderDto addOrder(Order order) {
		OrderEntity orderEntity = OrderMapper.mapToEntity(order);
		matchOrder(orderEntity);
		orderEntity = orderRepository.save(orderEntity);
		return OrderMapper.mapToDto(orderEntity);
	}

	@Override
	public List<OrderDto> getOrdersBySymbol(String symbol) {
		List<OrderEntity> orders = orderRepository.findBySymbolAndIsCompletedFalse(symbol);
		if (orders.isEmpty()) {
			throw new OrderNotFoundException("No orders found for symbol: " + symbol);
		}
		return orders.stream().map(OrderMapper::mapToDto).collect(Collectors.toList());
	}

	@Override
	public OrderDto getOrderById(String id) {
		return orderRepository.findById(id).map(OrderMapper::mapToDto)
				.orElseThrow(() -> new OrderNotFoundException("No orders found for this ID:" + id));
	}

	private void matchOrder(OrderEntity newOrder) {
		String oppositeType = newOrder.getOrderType().equals(OrderType.BUY.name()) ? OrderType.SELL.name()
				: OrderType.BUY.name();

		List<OrderEntity> potentialMatches = orderRepository
				.findBySymbolAndOrderTypeAndIsCompletedOrderByTimestampAsc(newOrder.getSymbol(), oppositeType, false);

		for (OrderEntity existing : potentialMatches) {
			if (!existing.getPrice().equals(newOrder.getPrice()))
				continue;
			if (newOrder.getRemainingQuantity() == 0)
				break;

			int matchQty = Math.min(existing.getRemainingQuantity(), newOrder.getRemainingQuantity());

			existing.setRemainingQuantity(existing.getRemainingQuantity() - matchQty);
			newOrder.setRemainingQuantity(newOrder.getRemainingQuantity() - matchQty);

			updateStatus(existing);
			updateStatus(newOrder);

			orderRepository.save(existing);
		}

		orderRepository.save(newOrder);
	}

	private void updateStatus(OrderEntity order) {
		if (order.getRemainingQuantity() == 0) {
			order.setStatus(OrderStatus.COMPLETED.name());
			order.setCompleted(true);
		} else if (order.getRemainingQuantity() < order.getQuantity()) {
			order.setStatus(OrderStatus.PARTIALLY_COMPLETED.name());
		} else {
			order.setStatus(OrderStatus.NONE.name());
		}
	}

}
