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
import org.example.warehouseinventory.warehouse.domain.StorageLocation;
import org.example.warehouseinventory.warehouse.domain.Warehouse;
import org.example.warehouseinventory.warehouse.infrastructure.StorageLocationRepository;
import org.example.warehouseinventory.warehouse.infrastructure.WarehouseRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryEntryServiceImpl implements InventoryEntryService {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final WarehouseRepository warehouseRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final LotRepository lotRepository;
    private final StockMovementRepository stockMovementRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional
    public LotResponse registerEntry(InventoryEntryRequest request) {

        Product _product = productMapper.toEntityResponse(productService.getProductById(request.product()));

        Warehouse _warehouse = warehouseRepository.findById(request.warehouse())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Warehouse not found with id: " + request.warehouse()
                ));

        StorageLocation location = storageLocationRepository
                .findByWarehouseIdAndAvailableTrue(request.warehouse())
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No available storage location in warehouse: " + request.warehouse()
                ));

        Lot lot = Lot.builder()
                .product(_product)
                .warehouse(_warehouse)
                .storageLocation(location)
                .lotNumber(request.lotNumber())
                .quantity(request.quantity())
                .expirationDate(request.expirationDate())
                .build();

        lotRepository.save(lot);

        location.addOccupancy(request.quantity());
        storageLocationRepository.save(location);

        String performedBy = Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getName();

        StockMovement movement = StockMovement.builder()
                .lot(lot)
                .type(MovementType.ENTRY)
                .quantity(request.quantity())
                .performedBy(performedBy)
                .notes(null)
                .build();

        stockMovementRepository.save(movement);
        return inventoryMapper.toDto(lot);
    }

}