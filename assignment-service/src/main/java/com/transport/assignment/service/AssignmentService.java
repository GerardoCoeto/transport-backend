package com.transport.assignment.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.transport.assignment.client.DriverClient;
import com.transport.assignment.client.OrderClient;
import com.transport.assignment.dto.AssignmentRequest;
import com.transport.assignment.dto.AssignmentResponse;
import com.transport.assignment.dto.DriverResponse;
import com.transport.assignment.dto.OrderResponse;
import com.transport.assignment.entity.Assignment;
import com.transport.assignment.exception.BusinessException;
import com.transport.assignment.repository.AssignmentRepository;

@Service
public class AssignmentService {

	private final AssignmentRepository assignmentRepository;
	private final OrderClient orderClient;
	private final DriverClient driverClient;

	public AssignmentService(AssignmentRepository assignmentRepository, OrderClient orderClient,
			DriverClient driverClient) {

		this.assignmentRepository = assignmentRepository;
		this.orderClient = orderClient;
		this.driverClient = driverClient;
	}

	public AssignmentResponse asignar(AssignmentRequest request) {

		OrderResponse order = orderClient.buscarPorId(request.orderId());

		if (!"CREATED".equals(order.status())) {
			throw new BusinessException("La orden debe estar en estado CREATED");
		}

		DriverResponse driver = driverClient.buscarPorId(request.driverId());

		if (!driver.active()) {
			throw new BusinessException("El conductor no está activo");
		}

		if (assignmentRepository.existsByOrderId(request.orderId())) {
			throw new BusinessException("La orden ya tiene un conductor asignado");
		}

		Assignment assignment = Assignment.builder().orderId(request.orderId()).driverId(request.driverId()).build();

		Assignment guardada = assignmentRepository.save(assignment);

		return convertirAResponse(guardada);
	}

	private AssignmentResponse convertirAResponse(Assignment assignment) {

		return new AssignmentResponse(assignment.getId(), assignment.getOrderId(), assignment.getDriverId(),
				assignment.getPdfPath(), assignment.getImagePath(), assignment.getAssignedAt());
	}

	public AssignmentResponse guardarPdf(UUID id, MultipartFile file) {

		Assignment assignment = assignmentRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Asignación no encontrada"));

		if (file.isEmpty()) {
			throw new BusinessException("El archivo PDF está vacío");
		}

		if (!"application/pdf".equals(file.getContentType())) {
			throw new BusinessException("Solo se permiten archivos PDF");
		}

		try {

			Path carpeta = Paths.get("uploads");

			if (!Files.exists(carpeta)) {
				Files.createDirectories(carpeta);
			}

			String nombreArchivo = UUID.randomUUID() + "_" + file.getOriginalFilename();

			Path ruta = carpeta.resolve(nombreArchivo);

			Files.copy(file.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);

			assignment.setPdfPath(ruta.toString());

			Assignment actualizada = assignmentRepository.save(assignment);

			return convertirAResponse(actualizada);

		} catch (IOException e) {

			throw new BusinessException("Error al guardar el archivo PDF");
		}
	}

	public AssignmentResponse guardarImagen(UUID id, MultipartFile file) {

		Assignment assignment = assignmentRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Asignación no encontrada"));

		if (file.isEmpty()) {
			throw new BusinessException("La imagen está vacía");
		}

		String contentType = file.getContentType();

		if (!"image/png".equals(contentType) && !"image/jpeg".equals(contentType)) {

			throw new BusinessException("Solo se permiten imágenes PNG o JPG");
		}

		try {

			Path carpeta = Paths.get("uploads");

			if (!Files.exists(carpeta)) {
				Files.createDirectories(carpeta);
			}

			String nombreArchivo = UUID.randomUUID() + "_" + file.getOriginalFilename();

			Path ruta = carpeta.resolve(nombreArchivo);

			Files.copy(file.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);

			assignment.setImagePath(ruta.toString());

			Assignment actualizada = assignmentRepository.save(assignment);

			return convertirAResponse(actualizada);

		} catch (IOException e) {

			throw new BusinessException("Error al guardar la imagen");
		}
	}
}