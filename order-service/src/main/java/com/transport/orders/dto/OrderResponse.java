package com.transport.orders.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.transport.orders.enums.OrderStatus;

public record OrderResponse(

    UUID id,
    OrderStatus status,
    String origin,
    String destination,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {
}