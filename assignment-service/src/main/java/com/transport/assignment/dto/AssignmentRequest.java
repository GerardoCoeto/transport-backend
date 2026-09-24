package com.transport.assignment.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AssignmentRequest(

        @NotNull(message = "El id de la orden es obligatorio")
        UUID orderId,

        @NotNull(message = "El id del conductor es obligatorio")
        UUID driverId

) {
}