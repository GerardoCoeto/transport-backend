package com.transport.driver.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transport.driver.entity.Driver;

public interface DriverRepository extends JpaRepository<Driver, UUID> {

    List<Driver> findByActiveTrue();
}