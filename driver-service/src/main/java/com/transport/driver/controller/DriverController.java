package com.transport.driver.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.transport.driver.dto.DriverRequest;
import com.transport.driver.dto.DriverResponse;
import com.transport.driver.service.DriverService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    public ResponseEntity<DriverResponse> guardar(
            @Valid @RequestBody DriverRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(driverService.guardar(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> buscarPorId(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                driverService.buscarPorId(id)
        );
    }

    @GetMapping("/active")
    public ResponseEntity<List<DriverResponse>> listarActivos() {

        return ResponseEntity.ok(
                driverService.listarActivos()
        );
    }
}