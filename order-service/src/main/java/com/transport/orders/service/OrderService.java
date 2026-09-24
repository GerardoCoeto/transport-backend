package com.transport.orders.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.transport.orders.dto.OrderRequest;
import com.transport.orders.dto.OrderResponse;
import com.transport.orders.dto.UpdateOrderStatusRequest;
import com.transport.orders.entity.Order;
import com.transport.orders.enums.OrderStatus;
import com.transport.orders.exception.BusinessException;
import com.transport.orders.exception.ResourceNotFoundException;
import com.transport.orders.repository.OrderRepository;
import com.transport.orders.specification.OrderSpecification;

@Service
public class OrderService {

	private final OrderRepository orderRepository;

	public OrderService(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	private boolean esTransicionValida(OrderStatus actual, OrderStatus nuevo) {

		return switch (actual) {

		case CREATED -> nuevo == OrderStatus.IN_TRANSIT || nuevo == OrderStatus.CANCELLED;

		case IN_TRANSIT -> nuevo == OrderStatus.DELIVERED || nuevo == OrderStatus.CANCELLED;

		case DELIVERED, CANCELLED -> false;
		};
	}

	public OrderResponse guardar(OrderRequest request) {

		Order order = Order.builder().origin(request.origin()).destination(request.destination()).build();

		Order saved = orderRepository.save(order);

		return convertirAResponse(saved);
	}

	private OrderResponse convertirAResponse(Order order) {

		return new OrderResponse(order.getId(), order.getStatus(), order.getOrigin(), order.getDestination(),
				order.getCreatedAt(), order.getUpdatedAt());
	}

	public OrderResponse buscarPorId(UUID id) {

		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id: " + id));

		return convertirAResponse(order);
	}

	public OrderResponse cambiarEstado(UUID id, UpdateOrderStatusRequest request) {

		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id: " + id));

		OrderStatus estadoActual = order.getStatus();
		OrderStatus nuevoEstado = request.status();

		if (!esTransicionValida(estadoActual, nuevoEstado)) {
			throw new BusinessException("No se puede cambiar la orden de " + estadoActual + " a " + nuevoEstado);
		}

		order.setStatus(nuevoEstado);

		Order actualizada = orderRepository.save(order);

		return convertirAResponse(actualizada);
	}

	public List<OrderResponse> listar(OrderStatus status, String origin, String destination, LocalDateTime fecha) {

		Specification<Order> specification = Specification.where(OrderSpecification.hasStatus(status))
				.and(OrderSpecification.hasOrigin(origin)).and(OrderSpecification.hasDestination(destination))
				.and(OrderSpecification.createdAfter(fecha));

		return orderRepository.findAll(specification).stream().map(this::convertirAResponse).toList();
	}
}