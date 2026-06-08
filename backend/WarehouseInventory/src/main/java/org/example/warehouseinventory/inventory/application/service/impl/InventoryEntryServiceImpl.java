package org.example.warehouseinventory.inventory.application.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.catalog.api.mapper.ProductMapper;
import org.example.warehouseinventory.catalog.application.service.ProductService;
import org.example.warehouseinventory.catalog.domain.entity.Product;
import org.example.warehouseinventory.inventory.api.mapper.InventoryMapper;
import org.example.warehouseinventory.inventory.application.service.InventoryEntryService;
import org.example.warehouseinventory.inventory.domain.dto.request.InventoryEntryRequest;
import org.example.warehouseinventory.inventory.domain.dto.response.LotResponse;
import org.example.warehouseinventory.inventory.domain.entity.Lot;
import org.example.warehouseinventory.inventory.domain.entity.StockMovement;
import org.example.warehouseinventory.inventory.infrastructure.repository.LotRepository;
import org.example.warehouseinventory.inventory.infrastructure.repository.StockMovementRepository;
import org.example.warehouseinventory.shared.api.exception.ResourceNotFoundException;
import org.example.warehouseinventory.shared.domain.enums.MovementType;
import org.example.warehouseinventory.warehouse.application.service.StorageLocationService;
import org.example.warehouseinventory.warehouse.application.service.WarehouseService;
import org.example.warehouseinventory.warehouse.application.service.impl.WarehouseServiceImpl;
import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;
import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;
import org.example.warehouseinventory.warehouse.domain.exception.NoAvailableStorageLocationException;
import org.example.warehouseinventory.warehouse.infrastructure.StorageLocationRepository;
import org.example.warehouseinventory.warehouse.infrastructure.WarehouseRepository;
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

    @Override
    @Transactional
    public LotResponse registerEntry(InventoryEntryRequest request) {

        Product _product = productMapper.toEntityResponse(productService.getProductById(request.product()));

        Warehouse _warehouse = warehouseService.getWarehouseById(request.warehouse());

        StorageLocation location = storageLocationService.findAvailableStorageLocation(
                request.warehouse(), request.quantity()
        );

        Lot lot = Lot.builder()
                .product(_product)
                .warehouse(_warehouse)
                .storageLocation(location)
                .lotNumber(request.lotNumber())
                .quantity(request.quantity())
                .availableQuantity(request.quantity())
                .expirationDate(request.expirationDate())
                .receivedAt(LocalDateTime.now())
                .build();

        lotRepository.save(lot);

        location.addOccupancy(request.quantity());
        storageLocationService.updateOccupancy(location, request.quantity());

        String performedBy = Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getName();

        StockMovement movement = StockMovement.builder()
                .lot(lot)
                .type(MovementType.ENTRY)
                .quantity(request.quantity())
                .performedBy(performedBy)
                .occurredAt(LocalDateTime.now())
                .notes(null)
                .build();

        stockMovementRepository.save(movement);
        return inventoryMapper.toDto(lot);
    }

}