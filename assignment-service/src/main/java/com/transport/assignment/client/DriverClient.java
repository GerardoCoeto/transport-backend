package com.transport.assignment.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.transport.assignment.config.FeignConfig;
import com.transport.assignment.dto.DriverResponse;

@FeignClient(name = "driver-service", configuration = FeignConfig.class, fallback = DriverClientFallback.class)
public interface DriverClient {

	@GetMapping("/drivers/{id}")
	DriverResponse buscarPorId(@PathVariable UUID id);
}
