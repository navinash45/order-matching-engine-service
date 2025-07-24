package com.order.engine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
public class OrderDto {

	private String id;
	private String symbol;
	private double price;
	private int quantity;
	private String type;
	private long timestamp;
	private String status;
}
