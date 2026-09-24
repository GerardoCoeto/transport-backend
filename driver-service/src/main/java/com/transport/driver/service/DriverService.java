package com.transport.driver.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.transport.driver.dto.DriverRequest;
import com.transport.driver.dto.DriverResponse;
import com.transport.driver.entity.Driver;
import com.transport.driver.repository.DriverRepository;

@Service
public class DriverService {

	private final DriverRepository driverRepository;

	public DriverService(DriverRepository driverRepository) {
		this.driverRepository = driverRepository;
	}

	public DriverResponse guardar(DriverRequest request) {

		Driver driver = Driver.builder().name(request.name()).licenseNumber(request.licenseNumber())
				.active(request.active()).build();

		Driver guardado = driverRepository.save(driver);

		return convertirAResponse(guardado);
	}

	public DriverResponse buscarPorId(UUID id) {

		Driver driver = driverRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Conductor no encontrado"));

		return convertirAResponse(driver);
	}

	public List<DriverResponse> listarActivos() {

		return driverRepository.findByActiveTrue().stream().map(this::convertirAResponse).toList();
	}

	private DriverResponse convertirAResponse(Driver driver) {

		return new DriverResponse(driver.getId(), driver.getName(), driver.getLicenseNumber(), driver.isActive());
	}
}