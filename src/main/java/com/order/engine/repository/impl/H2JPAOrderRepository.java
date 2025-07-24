package com.order.engine.repository.impl;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.order.engine.entity.OrderEntity;

@Repository
public interface H2JPAOrderRepository extends JpaRepository<OrderEntity, String> {

	List<OrderEntity> findBySymbolAndOrderTypeAndIsCompletedOrderByTimestampAsc(String symbol, String oppositeType,
			boolean isCompleted);

	List<OrderEntity> findBySymbolAndIsCompletedFalse(String symbol);

}
