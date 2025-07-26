package com.order.engine.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.order.engine.dto.OrderDto;
import com.order.engine.dto.OrderRequest;
import com.order.engine.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/apis/v1/orders")
public class OrderController {

	private final OrderService service;

	public OrderController(OrderService service) {
		this.service = service;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public OrderDto addOrder(@Valid @RequestBody OrderRequest orderRequest) {
		return service.addOrder(orderRequest);
	}

	@GetMapping("/{id}")
	public OrderDto getOrder(@PathVariable String id) {

		return service.getOrder(id);
	}

	@GetMapping("/symbol/{symbol}")
	public List<OrderDto> getOrdersBySymbol(@PathVariable String symbol) {
		return service.getOrdersBySymbol(symbol);
	}
}
