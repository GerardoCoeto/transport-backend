package com.transport.orders.dto;

import com.transport.orders.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(

    @NotNull(message = "El estado es obligatorio")
    OrderStatus status

) {
}