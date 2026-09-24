package com.transport.orders.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.transport.orders.dto.OrderRequest;
import com.transport.orders.dto.OrderResponse;
import com.transport.orders.dto.UpdateOrderStatusRequest;
import com.transport.orders.enums.OrderStatus;
import com.transport.orders.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	public ResponseEntity<OrderResponse> guardar(@Valid @RequestBody OrderRequest request) {

		OrderResponse response = orderService.guardar(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<OrderResponse> buscarPorId(@PathVariable UUID id) {

		return ResponseEntity.ok(orderService.buscarPorId(id));
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<OrderResponse> cambiarEstado(@PathVariable UUID id,
			@Valid @RequestBody UpdateOrderStatusRequest request) {

		return ResponseEntity.ok(orderService.cambiarEstado(id, request));
	}

	@GetMapping
	public ResponseEntity<List<OrderResponse>> listar(

			@RequestParam(required = false) OrderStatus status,

			@RequestParam(required = false) String origin,

			@RequestParam(required = false) String destination,

			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha) {

		return ResponseEntity.ok(orderService.listar(status, origin, destination, fecha));
	}
}