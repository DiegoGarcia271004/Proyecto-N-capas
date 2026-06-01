package org.example.warehouseinventory.inventory.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.inventory.application.service.InventoryEntryService;
import org.example.warehouseinventory.inventory.application.service.StockConsumptionService;
import org.example.warehouseinventory.inventory.domain.dto.request.InventoryEntryRequest;
import org.example.warehouseinventory.inventory.domain.dto.request.StockConsumptionRequest;
import org.example.warehouseinventory.shared.api.BaseController;
import org.example.warehouseinventory.shared.domain.GeneralResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController extends BaseController {

    private final InventoryEntryService inventoryEntryService;
    private final StockConsumptionService stockConsumptionService;

    @PostMapping("/entry")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> registerEntry(
            @Valid @RequestBody InventoryEntryRequest request
    ) {

        return buildResponse(
                "Inventory entry registered successfully.",
                HttpStatus.CREATED,
                inventoryEntryService.registerEntry(request)
        );
    }

    @PostMapping("/consume")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> consumeStock(
            @Valid @RequestBody StockConsumptionRequest request
    ) {

        stockConsumptionService.consumeStock(request);

        return buildResponse(
                "Stock consumed succesfully.",
                HttpStatus.OK,
                null
        );
    }
}