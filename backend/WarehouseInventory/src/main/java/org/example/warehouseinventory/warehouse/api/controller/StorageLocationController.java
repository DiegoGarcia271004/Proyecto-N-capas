package org.example.warehouseinventory.warehouse.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.shared.api.BaseController;
import org.example.warehouseinventory.shared.domain.GeneralResponse;
import org.example.warehouseinventory.warehouse.application.service.StorageLocationService;
import org.example.warehouseinventory.warehouse.domain.dto.request.CreateStorageLocationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/storage-location")
@RequiredArgsConstructor
public class StorageLocationController extends BaseController {

    private final StorageLocationService storageLocationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse> create(
            @Valid @RequestBody CreateStorageLocationRequest request
    ) {
        return buildResponse(
                "Storage location created successfully",
                HttpStatus.CREATED,
                storageLocationService.createLocation(request)
        );
    }

    @GetMapping("/warehouse/{warehouse}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> getByWarehouse(
            @PathVariable UUID warehouse
    ) {
        return buildResponse(
                "Storage locations retrieved successfully",
                HttpStatus.OK,
                storageLocationService.getByWarehouse(warehouse)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> getById(
            @PathVariable UUID id
    ) {
        return buildResponse(
                "Storage location retrieved successfully",
                HttpStatus.OK,
                storageLocationService.getLocationById(id)
        );
    }
}