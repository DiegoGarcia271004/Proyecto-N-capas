package org.example.warehouseinventory.warehouse.api.controller;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.shared.api.BaseController;
import org.example.warehouseinventory.shared.domain.GeneralResponse;
import org.example.warehouseinventory.shared.domain.enums.AssignmentStrategy;
import org.example.warehouseinventory.warehouse.application.service.WarehousePolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/warehouse-policy")
@RequiredArgsConstructor
public class WarehousePolicyController extends BaseController {

    private final WarehousePolicyService warehousePolicyService;

    @GetMapping("/{warehouse}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> getPolicy(
            @PathVariable UUID warehouse
    ) {

        return buildResponse(
                "Policy retrieved successfully",
                HttpStatus.OK,
                warehousePolicyService.getOrDefault(warehouse)
        );
    }

    @PutMapping("/{warehouse}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralResponse> setStrategy(
            @PathVariable UUID warehouse,
            @RequestParam AssignmentStrategy strategy
    ) {

        return buildResponse(
                "Strategy updated successfully",
                HttpStatus.OK,
                warehousePolicyService.setStrategy(warehouse, strategy)
        );
    }
}