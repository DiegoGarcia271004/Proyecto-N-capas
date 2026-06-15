package org.example.warehouseinventory.inventory.application.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.catalog.api.mapper.ProductMapper;
import org.example.warehouseinventory.catalog.application.service.ProductService;
import org.example.warehouseinventory.catalog.domain.entity.Product;
import org.example.warehouseinventory.inventory.api.mapper.InventoryMapper;
import org.example.warehouseinventory.inventory.application.service.InventoryEntryService;
import org.example.warehouseinventory.inventory.application.service.ProductCostService;
import org.example.warehouseinventory.inventory.domain.dto.request.InventoryEntryRequest;
import org.example.warehouseinventory.inventory.domain.dto.response.LotResponse;
import org.example.warehouseinventory.inventory.domain.entity.Lot;
import org.example.warehouseinventory.inventory.domain.entity.StockMovement;
import org.example.warehouseinventory.inventory.infrastructure.repository.LotRepository;
import org.example.warehouseinventory.inventory.infrastructure.repository.StockMovementRepository;
import org.example.warehouseinventory.shared.domain.enums.MovementType;
import org.example.warehouseinventory.warehouse.application.service.StorageLocationService;
import org.example.warehouseinventory.warehouse.application.service.WarehouseService;
import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;
import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InventoryEntryServiceImpl implements InventoryEntryService {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final WarehouseService warehouseService;
    private final StorageLocationService storageLocationService;
    private final LotRepository lotRepository;
    private final StockMovementRepository stockMovementRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductCostService productCostService;

    @Override
    @Transactional
    public LotResponse registerEntry(InventoryEntryRequest request) {

        Product _product = productService.getProductEntityById(request.product());

        Warehouse _warehouse = warehouseService.getWarehouseById(request.warehouse());

        StorageLocation location = storageLocationService.findAvailableStorageLocation(
                request.warehouse(), request.quantity()
        );

        Lot lot = Lot.create(
                _product,
                _warehouse,
                location,
                request.lotNumber(),
                request.quantity(),
                request.expirationDate());

        lotRepository.save(lot);
        storageLocationService.updateOccupancy(location, request.quantity());

        StockMovement movement = StockMovement.create(lot,
                MovementType.ENTRY,
                request.quantity(),
                null);

        stockMovementRepository.save(movement);

        productCostService.recalculate(
                request.product(),
                request.warehouse(),
                request.unitCost(),
                request.quantity()
        );

        return inventoryMapper.toDto(lot);
    }

}