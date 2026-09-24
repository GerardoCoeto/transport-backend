package com.transport.orders.specification;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.transport.orders.entity.Order;
import com.transport.orders.enums.OrderStatus;

public class OrderSpecification {

	public static Specification<Order> hasStatus(OrderStatus status) {
		return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
	}

	public static Specification<Order> hasOrigin(String origin) {
		return (root, query, cb) -> origin == null || origin.isBlank() ? null
				: cb.like(cb.lower(root.get("origin")), "%" + origin.toLowerCase() + "%");
	}

	public static Specification<Order> hasDestination(String destination) {
		return (root, query, cb) -> destination == null || destination.isBlank() ? null
				: cb.like(cb.lower(root.get("destination")), "%" + destination.toLowerCase() + "%");
	}

	public static Specification<Order> createdAfter(LocalDateTime fecha) {
		return (root, query, cb) -> fecha == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), fecha);
	}
}