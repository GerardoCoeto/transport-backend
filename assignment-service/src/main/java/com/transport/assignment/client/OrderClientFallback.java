package com.transport.assignment.client;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.transport.assignment.dto.OrderResponse;
import com.transport.assignment.exception.BusinessException;

@Component
public class OrderClientFallback implements OrderClient {

    @Override
    public OrderResponse buscarPorId(UUID id) {

        throw new BusinessException(
                "order-service no está disponible temporalmente"
        );
    }
}