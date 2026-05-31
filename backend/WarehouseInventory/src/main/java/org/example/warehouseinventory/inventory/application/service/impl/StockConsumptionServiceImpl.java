package org.example.warehouseinventory.inventory.application.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.catalog.api.mapper.ProductMapper;
import org.example.warehouseinventory.catalog.application.service.ProductService;
import org.example.warehouseinventory.inventory.application.service.StockConsumptionService;
import org.example.warehouseinventory.inventory.domain.entity.Lot;
import org.example.warehouseinventory.inventory.domain.entity.StockMovement;
import org.example.warehouseinventory.inventory.domain.exception.InsufficientStockException;
import org.example.warehouseinventory.inventory.infrastructure.repository.LotRepository;
import org.example.warehouseinventory.inventory.infrastructure.repository.StockMovementRepository;
import org.example.warehouseinventory.shared.domain.enums.MovementType;
import org.example.warehouseinventory.warehouse.infrastructure.StorageLocationRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockConsumptionServiceImpl implements StockConsumptionService {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final LotRepository lotRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StorageLocationRepository storageLocationRepository;

    @Override
    @Transactional
    public void consumeStock(UUID product, UUID warehouse, Integer quantityRequested) {

        productService.getProductById(product);

        List<Lot> lots = lotRepository.findAvailableLotsFifo(product, warehouse);

        int totalAvailable = lots.stream()
                .mapToInt(Lot::getAvailableQuantity)
                .sum();

        if (totalAvailable < quantityRequested)
            throw new InsufficientStockException(
                    product.toString(), quantityRequested, totalAvailable
            );

        String performedBy = Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getName();

        int remaining = quantityRequested;

        for (Lot lot : lots) {

            if (remaining == 0) break;

            int toConsume = Math.min(remaining, lot.getAvailableQuantity());
            lot.consumeUnits(toConsume);
            lotRepository.save(lot);

            lot.getStorageLocation().removeOccupancy(toConsume);
            storageLocationRepository.save(lot.getStorageLocation());

            StockMovement movement = StockMovement.builder()
                    .lot(lot)
                    .type(MovementType.EXIT)
                    .quantity(toConsume)
                    .performedBy(performedBy)
                    .notes(null)
                    .build();

            stockMovementRepository.save(movement);
            remaining -= toConsume;
        }
    }
}