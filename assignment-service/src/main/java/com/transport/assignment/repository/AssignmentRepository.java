package com.transport.assignment.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transport.assignment.entity.Assignment;

public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {

	boolean existsByOrderId(UUID orderId);
}