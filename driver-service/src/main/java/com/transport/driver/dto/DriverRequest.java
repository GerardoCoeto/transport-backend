package com.transport.driver.dto;

import jakarta.validation.constraints.NotBlank;

public record DriverRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotBlank(message = "La licencia es obligatoria")
        String licenseNumber,

        boolean active

) {
}