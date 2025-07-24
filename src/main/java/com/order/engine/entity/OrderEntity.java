package com.order.engine.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
@Table(name = "orders")
public class OrderEntity {
	@Id
	private String id;
	private String symbol;
	private Double price;
	private int quantity;
	private String orderType;
	private long timestamp;
	private String status;
	private int remainingQuantity;
	private boolean isCompleted;

	@PrePersist
	public void generateId() {
		if (this.id == null) {
			this.id = UUID.randomUUID().toString();
		}
	}
}
