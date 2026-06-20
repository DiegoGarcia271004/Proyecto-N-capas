package org.example.warehouseinventory.inventory.application.service.impl;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.catalog.api.mapper.ProductMapper;
import org.example.warehouseinventory.catalog.application.service.ProductService;
import org.example.warehouseinventory.inventory.application.service.StockConsumptionService;
import org.example.warehouseinventory.inventory.domain.dto.request.StockConsumptionRequest;
import org.example.warehouseinventory.inventory.domain.entity.Lot;
import org.example.warehouseinventory.inventory.domain.entity.StockMovement;
import org.example.warehouseinventory.inventory.domain.exception.InsufficientStockException;
import org.example.warehouseinventory.inventory.infrastructure.repository.LotRepository;
import org.example.warehouseinventory.inventory.infrastructure.repository.StockMovementRepository;
import org.example.warehouseinventory.shared.domain.enums.MovementType;
import org.example.warehouseinventory.warehouse.application.service.StorageLocationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StockConsumptionServiceImpl implements StockConsumptionService {

    private final ProductService productService;
    private final LotRepository lotRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StorageLocationService storageLocationService;

    @Override
    @Transactional
    public void consumeStock(StockConsumptionRequest request) {

        productService.getProductEntityById(request.product());

        List<Lot> lots = lotRepository.findAvailableLotsFifo(request.product(), request.warehouse());

        int totalAvailable = lots.stream()
                .mapToInt(Lot::getAvailableQuantity)
                .sum();

        if (totalAvailable < request.quantity())
            throw new InsufficientStockException(
                    request.product().toString(), request.quantity(), totalAvailable
            );

        String performedBy = Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getName();

        int remaining = request.quantity();

        for (Lot lot : lots) {

            if (remaining == 0) break;

            int toConsume = Math.min(remaining, lot.getAvailableQuantity());
            lot.consumeUnits(toConsume);
            lotRepository.save(lot);

            storageLocationService.releaseOccupancy(lot.getStorageLocation(), toConsume);

            StockMovement movement = StockMovement.create(
                    lot,
                    MovementType.EXIT,
                    toConsume,
                    "",
                    performedBy
            );

            stockMovementRepository.save(movement);
            remaining -= toConsume;
        }
    }
}