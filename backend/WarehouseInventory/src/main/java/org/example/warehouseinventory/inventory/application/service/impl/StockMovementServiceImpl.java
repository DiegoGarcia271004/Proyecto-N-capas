package org.example.warehouseinventory.inventory.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.inventory.application.service.StockMovementService;
import org.example.warehouseinventory.inventory.domain.dto.response.ProductWarehouseExitSummary;
import org.example.warehouseinventory.inventory.domain.entity.Lot;
import org.example.warehouseinventory.inventory.domain.entity.StockMovement;
import org.example.warehouseinventory.inventory.infrastructure.repository.StockMovementRepository;
import org.example.warehouseinventory.shared.domain.enums.MovementType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

    @Override
    public Integer getTotalExit(UUID product, UUID warehouse) {

        return stockMovementRepository.getTotalExitQuantityLast90Days(product, warehouse);
    }

    @Override
    @Transactional
    public void recordAdjustment(Lot lot, Integer discrepancy) {
        String performedBy = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()).getName();

        StockMovement movement = StockMovement.create(
                lot,
                MovementType.ADJUSTMENT,
                Math.abs(discrepancy),
                discrepancy > 0 ? "Cyclic count adjustment — surplus" :
                        "Cyclic count adjustment — shortage"
        );

        stockMovementRepository.save(movement);
    }
}
