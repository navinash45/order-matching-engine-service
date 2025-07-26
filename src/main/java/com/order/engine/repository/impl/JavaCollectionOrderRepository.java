package com.order.engine.repository.impl;

import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.order.engine.config.OrderMetrics;
import com.order.engine.dto.OrderDto;
import com.order.engine.entity.Order;
import com.order.engine.exception.OrderNotFoundException;
import com.order.engine.repository.OrderRepository;
import com.order.engine.util.OrderMapper;

@Repository
@Profile("!db")
public class JavaCollectionOrderRepository implements OrderRepository {

	@Autowired
	private OrderMetrics orderMetrics;

	private final ConcurrentHashMap<String, Order> allOrders = new ConcurrentHashMap<>();

	private final PriorityQueue<Order> buyOrders = new PriorityQueue<>(11, (o1, o2) -> {
		int priceCompare = Double.compare(o2.getPrice(), o1.getPrice());
		if (priceCompare == 0) {
			return Long.compare(o1.getTimestamp(), o2.getTimestamp());
		}
		return priceCompare;
	});
	private final PriorityQueue<Order> sellOrders = new PriorityQueue<>(11, (o1, o2) -> {
		int priceCompare = Double.compare(o1.getPrice(), o2.getPrice());
		if (priceCompare == 0) {
			return Long.compare(o1.getTimestamp(), o2.getTimestamp());
		}
		return priceCompare;
	});

	private void matchOrders(Order newOrder) {
		PriorityQueue<Order> oppositeQueue = newOrder.getOrderType().equalsIgnoreCase("BUY") ? sellOrders : buyOrders;

		while (!oppositeQueue.isEmpty() && newOrder.getRemainingQuantity() > 0) {
			Order topOrder = oppositeQueue.peek();

			boolean match = newOrder.getOrderType().equalsIgnoreCase("BUY") ? newOrder.getPrice() >= topOrder.getPrice()
					: newOrder.getPrice() <= topOrder.getPrice();

			if (!match)
				break;

			int tradedQty = Math.min(newOrder.getRemainingQuantity(), topOrder.getRemainingQuantity());

			newOrder.setRemainingQuantity(newOrder.getRemainingQuantity() - tradedQty);
			topOrder.setRemainingQuantity(topOrder.getRemainingQuantity() - tradedQty);

			updateStatus(newOrder);
			updateStatus(topOrder);
			orderMetrics.recordMatch();
			
			if (topOrder.getRemainingQuantity() == 0) {
				oppositeQueue.poll();
			}
		}

		if (newOrder.getQuantity() == newOrder.getRemainingQuantity()) {
			newOrder.setStatus("NONE");
		}
	}

	private void updateStatus(Order order) {
		if (order.getRemainingQuantity() == 0) {
			order.setStatus("COMPLETE");
		} else if (order.getRemainingQuantity() < order.getQuantity()) {
			order.setStatus("PARTIAL");
		} else {
			order.setStatus("NONE");
		}
	}

	@Cacheable(value = "ordersById", key = "#id")
	@Override
	public OrderDto getOrderById(String id) {
		return allOrders.entrySet().stream().filter(e -> id.equalsIgnoreCase(e.getKey()))
				.map(e -> OrderMapper.convertToDTO(e.getValue())).findFirst()
				.orElseThrow(() -> new OrderNotFoundException("No orders found for this ID:" + id));
	}

	@Cacheable(value = "ordersBySymbol", key = "#symbol")
	@Override
	public List<OrderDto> getOrdersBySymbol(String symbol) {
		List<OrderDto> matchingOrders = allOrders.values().stream().filter(e -> symbol.equalsIgnoreCase(e.getSymbol()))
				.map(OrderMapper::convertToDTO).toList();
		if (matchingOrders.isEmpty()) {
			throw new OrderNotFoundException("No orders found for symbol: " + symbol);
		}
		return matchingOrders;
	}

	@Override
	@CacheEvict(value = { "ordersById", "ordersBySymbol" }, allEntries = true)
	public synchronized OrderDto addOrder(Order newOrder) {
		orderMetrics.recordOrder(newOrder.getOrderType());
		matchOrders(newOrder);
		allOrders.put(newOrder.getId(), newOrder);
		if (newOrder.getRemainingQuantity() > 0) {
			if (newOrder.getOrderType().equalsIgnoreCase("BUY")) {
				buyOrders.add(newOrder);
			} else {
				sellOrders.add(newOrder);
			}
		}
		return OrderMapper.convertToDTO(newOrder);
	}

}