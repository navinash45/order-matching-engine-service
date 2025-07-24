package com.order.engine.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class Order {
	private String id;
	private String symbol;
	private double price;
	private int quantity;
	private String orderType;
	private long timestamp;
	private String status;
	private int remainingQuantity;

}
