package org.example.warehouseinventory.inventory.application.service;

import org.example.warehouseinventory.inventory.domain.dto.response.ProductWarehouseExitSummary;

import java.time.LocalDate;
import java.util.List;

public interface StockMovementService {
    List<ProductWarehouseExitSummary> getExitSummaryByProductAndWarehouse(LocalDate from, LocalDate to);
}
