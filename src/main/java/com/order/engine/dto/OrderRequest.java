package com.order.engine.dto;

import com.order.engine.constants.OrderType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@Setter
@Getter
public class OrderRequest {

	@NotBlank
	private String symbol;

	@Positive
	private double price;

	@Positive
	private int quantity;

	@NotNull
	private OrderType type;
}
