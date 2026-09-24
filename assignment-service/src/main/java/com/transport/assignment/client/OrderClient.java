package com.transport.assignment.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.transport.assignment.config.FeignConfig;
import com.transport.assignment.dto.OrderResponse;

@FeignClient(name = "order-service", configuration = FeignConfig.class, fallback = OrderClientFallback.class)
public interface OrderClient {

	@GetMapping("/orders/{id}")
	OrderResponse buscarPorId(@PathVariable UUID id);
}