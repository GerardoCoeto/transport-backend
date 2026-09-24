package com.transport.assignment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssignmentResponse(
        UUID id,
        UUID orderId,
        UUID driverId,
        String pdfPath,
        String imagePath,
        LocalDateTime assignedAt
) {
}