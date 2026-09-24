package com.transport.assignment.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.transport.assignment.dto.DriverResponse;
import com.transport.assignment.exception.BusinessException;

@Component
public class DriverClientFallback implements DriverClient {

	@Override
	public DriverResponse buscarPorId(UUID id) {

		throw new BusinessException("driver-service no está disponible temporalmente");
	}
}