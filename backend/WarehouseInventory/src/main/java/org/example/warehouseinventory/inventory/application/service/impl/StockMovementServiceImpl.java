package org.example.warehouseinventory.inventory.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.inventory.application.service.StockMovementService;
import org.example.warehouseinventory.inventory.domain.dto.response.ProductWarehouseExitSummary;
import org.example.warehouseinventory.inventory.infrastructure.repository.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;

    @Override
    public List<ProductWarehouseExitSummary> getExitSummaryByProductAndWarehouse(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.plusDays(1).atStartOfDay();
        return stockMovementRepository.sumExitByProductAndWarehouse(fromDt, toDt)
                .stream()
                .map(p -> new ProductWarehouseExitSummary(p.getProductId(), p.getWarehouseId(), p.getTotalExitQuantity()))
                .toList();
    }
}
