package org.example.warehouseinventory.inventory.application.service;

import org.example.warehouseinventory.inventory.domain.dto.response.ProductWarehouseExitSummary;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface StockMovementService {

    List<ProductWarehouseExitSummary> getExitSummaryByProductAndWarehouse(LocalDate from, LocalDate to);
    Integer getTotalExit(UUID product, UUID warehouse);
}
