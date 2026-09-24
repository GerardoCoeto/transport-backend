package com.transport.orders.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.transport.orders.dto.OrderRequest;
import com.transport.orders.dto.UpdateOrderStatusRequest;
import com.transport.orders.entity.Order;
import com.transport.orders.enums.OrderStatus;
import com.transport.orders.exception.BusinessException;
import com.transport.orders.exception.ResourceNotFoundException;
import com.transport.orders.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private OrderService orderService;

	private Order order;

	@BeforeEach
	void setUp() {

		order = Order.builder().id(UUID.randomUUID()).origin("Puebla").destination("Veracruz")
				.status(OrderStatus.CREATED).build();
	}

	@Test
	void guardarOrden() {

		OrderRequest request = new OrderRequest("Puebla", "Veracruz");

		when(orderRepository.save(any(Order.class))).thenReturn(order);

		var response = orderService.guardar(request);

		assertEquals("Puebla", response.origin());

		assertEquals(OrderStatus.CREATED, response.status());
	}

	@Test
	void buscarOrdenNoExistente() {

		UUID id = UUID.randomUUID();

		when(orderRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> orderService.buscarPorId(id));
	}

	@Test
	void cambiarEstadoDeCreatedAInTransit() {

		UUID id = UUID.randomUUID();

		Order order = Order.builder().id(id).origin("Puebla").destination("Veracruz").status(OrderStatus.CREATED)
				.build();

		when(orderRepository.findById(id)).thenReturn(Optional.of(order));

		when(orderRepository.save(any(Order.class))).thenReturn(order);

		var request = new UpdateOrderStatusRequest(OrderStatus.IN_TRANSIT);

		var response = orderService.cambiarEstado(id, request);

		assertEquals(OrderStatus.IN_TRANSIT, response.status());
	}

	@Test
	void noPermitirCambiarDelivered() {

		UUID id = UUID.randomUUID();

		Order order = Order.builder().id(id).origin("Puebla").destination("Veracruz").status(OrderStatus.DELIVERED)
				.build();

		when(orderRepository.findById(id)).thenReturn(Optional.of(order));

		var request = new UpdateOrderStatusRequest(OrderStatus.CANCELLED);

		assertThrows(BusinessException.class, () -> orderService.cambiarEstado(id, request));
	}
}