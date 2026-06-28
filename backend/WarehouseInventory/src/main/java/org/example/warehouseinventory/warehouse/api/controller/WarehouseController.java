package org.example.warehouseinventory.warehouse.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.shared.api.BaseController;
import org.example.warehouseinventory.shared.domain.GeneralResponse;
import org.example.warehouseinventory.warehouse.application.service.WarehouseService;
import org.example.warehouseinventory.warehouse.domain.dto.request.CreateWarehouseRequest;
import org.example.warehouseinventory.warehouse.domain.dto.request.UpdateWarehouseRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseController extends BaseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse> create(
            @Valid @RequestBody CreateWarehouseRequest request
    ) {
        return buildResponse(
                "Warehouse created successfully",
                HttpStatus.CREATED,
                warehouseService.create(request)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> getAll() {
        return buildResponse(
                "Warehouses retrieved successfully",
                HttpStatus.OK,
                warehouseService.getAll()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> getById(
            @PathVariable UUID id
    ) {
        return buildResponse(
                "Warehouse retrieved successfully",
                HttpStatus.OK,
                warehouseService.getById(id)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateWarehouseRequest request
    ) {
        return buildResponse(
                "Warehouse updated successfully",
                HttpStatus.OK,
                warehouseService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse> deactivate(
            @PathVariable UUID id
    ) {
        return buildResponse(
                "Warehouse deactivated successfully",
                HttpStatus.OK,
                warehouseService.deactivate(id)
        );
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse> activate(
            @PathVariable UUID id
    ) {
        return buildResponse(
                "Warehouse activated successfully",
                HttpStatus.OK,
                warehouseService.activate(id)
        );
    }
}