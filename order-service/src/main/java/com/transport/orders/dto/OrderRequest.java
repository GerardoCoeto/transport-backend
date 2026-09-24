package com.transport.orders.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderRequest(

    @NotBlank(message = "El origen es obligatorio")
    String origin,

    @NotBlank(message = "El destino es obligatorio")
    String destination

) {
}