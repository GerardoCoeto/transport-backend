package com.transport.assignment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.transport.assignment.client.DriverClient;
import com.transport.assignment.client.OrderClient;
import com.transport.assignment.dto.AssignmentRequest;
import com.transport.assignment.dto.DriverResponse;
import com.transport.assignment.dto.OrderResponse;
import com.transport.assignment.entity.Assignment;
import com.transport.assignment.exception.BusinessException;
import com.transport.assignment.repository.AssignmentRepository;

@ExtendWith(MockitoExtension.class)
public class AssignmentServiceTest {

	@Mock
	private AssignmentRepository assignmentRepository;

	@Mock
	private OrderClient orderClient;

	@Mock
	private DriverClient driverClient;

	@InjectMocks
	private AssignmentService assignmentService;

	private UUID orderId;
	private UUID driverId;

	@BeforeEach
	void setUp() {

		orderId = UUID.randomUUID();
		driverId = UUID.randomUUID();
	}

	@Test
	void asignarConductorCorrectamente() {

		OrderResponse order = new OrderResponse(orderId, "CREATED", "Puebla", "Veracruz", null, null);

		DriverResponse driver = new DriverResponse(driverId, "Carlos Hernandez", "LIC-1001", true);

		Assignment assignment = Assignment.builder().id(UUID.randomUUID()).orderId(orderId).driverId(driverId).build();

		when(orderClient.buscarPorId(orderId)).thenReturn(order);

		when(driverClient.buscarPorId(driverId)).thenReturn(driver);

		when(assignmentRepository.existsByOrderId(orderId)).thenReturn(false);

		when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);

		AssignmentRequest request = new AssignmentRequest(orderId, driverId);

		var response = assignmentService.asignar(request);

		assertEquals(orderId, response.orderId());

		assertEquals(driverId, response.driverId());
	}

	@Test
	void noAsignarSiOrdenNoEstaCreated() {

		OrderResponse order = new OrderResponse(orderId, "IN_TRANSIT", "Puebla", "Veracruz", null, null);

		when(orderClient.buscarPorId(orderId)).thenReturn(order);

		AssignmentRequest request = new AssignmentRequest(orderId, driverId);

		assertThrows(BusinessException.class, () -> assignmentService.asignar(request));
	}

	@Test
	void noAsignarConductorInactivo() {

		OrderResponse order = new OrderResponse(orderId, "CREATED", "Puebla", "Veracruz", null, null);

		DriverResponse driver = new DriverResponse(driverId, "Miguel Ramirez", "LIC-1002", false);

		when(orderClient.buscarPorId(orderId)).thenReturn(order);

		when(driverClient.buscarPorId(driverId)).thenReturn(driver);

		AssignmentRequest request = new AssignmentRequest(orderId, driverId);

		assertThrows(BusinessException.class, () -> assignmentService.asignar(request));
	}

	@Test
	void noAsignarDosVecesLaMismaOrden() {

		OrderResponse order = new OrderResponse(orderId, "CREATED", "Puebla", "Veracruz", null, null);

		DriverResponse driver = new DriverResponse(driverId, "Carlos Hernandez", "LIC-1001", true);

		when(orderClient.buscarPorId(orderId)).thenReturn(order);

		when(driverClient.buscarPorId(driverId)).thenReturn(driver);

		when(assignmentRepository.existsByOrderId(orderId)).thenReturn(true);

		AssignmentRequest request = new AssignmentRequest(orderId, driverId);

		assertThrows(BusinessException.class, () -> assignmentService.asignar(request));
	}
}