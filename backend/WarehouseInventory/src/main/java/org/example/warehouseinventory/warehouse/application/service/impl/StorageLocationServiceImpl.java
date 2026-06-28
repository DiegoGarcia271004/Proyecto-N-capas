package org.example.warehouseinventory.warehouse.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.shared.api.exception.BusinessRuleViolationException;
import org.example.warehouseinventory.shared.api.exception.ResourceNotFoundException;
import org.example.warehouseinventory.shared.domain.enums.AssignmentStrategy;
import org.example.warehouseinventory.shared.domain.enums.StorageRequirement;
import org.example.warehouseinventory.warehouse.api.mapper.StorageLocationMapper;
import org.example.warehouseinventory.warehouse.application.service.StorageLocationService;
import org.example.warehouseinventory.warehouse.application.service.WarehousePolicyService;
import org.example.warehouseinventory.warehouse.application.service.WarehouseService;
import org.example.warehouseinventory.warehouse.application.strategy.LocationAssignmentStrategy;
import org.example.warehouseinventory.warehouse.domain.dto.request.CreateStorageLocationRequest;
import org.example.warehouseinventory.warehouse.domain.dto.response.StorageLocationResponse;
import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;
import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;
import org.example.warehouseinventory.warehouse.domain.exception.NoAvailableStorageLocationException;
import org.example.warehouseinventory.warehouse.infrastructure.StorageLocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageLocationServiceImpl implements StorageLocationService {

    private final StorageLocationRepository storageLocationRepository;
    private final WarehousePolicyService warehousePolicyService;
    private final List<LocationAssignmentStrategy> strategies;
    private final StorageLocationMapper storageLocationMapper;
    private final WarehouseService warehouseService;

    @Override
    public StorageLocation findAvailableStorageLocation(UUID warehouse, Integer quantity, StorageRequirement requirement) {

        List<StorageLocation> available = storageLocationRepository
                .findAvailableByCapacity(warehouse, quantity);

        if (available.isEmpty()) {
            Integer totalCapacity = storageLocationRepository
                    .getCapacityAvailableByWarehouse(warehouse);
            throw new NoAvailableStorageLocationException(warehouse, quantity, totalCapacity);
        }

        AssignmentStrategy strategyName = warehousePolicyService
                .getOrDefault(warehouse).getStrategyName();

        LocationAssignmentStrategy strategy = strategies.stream()
                .filter(s -> s.strategyName() == strategyName)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Strategy not found: " + strategyName));

        return strategy.assign(available, requirement);
    }

    @Override
    @Transactional
    public void updateOccupancy(StorageLocation location, int units) {
        location.addOccupancy(units);
        storageLocationRepository.save(location);
    }

    @Override
    @Transactional
    public void releaseOccupancy(StorageLocation location, int units) {
        location.removeOccupancy(units);
        storageLocationRepository.save(location);
    }

    @Override
    @Transactional
    public StorageLocationResponse createLocation(CreateStorageLocationRequest request) {

        Warehouse warehouse = warehouseService.getWarehouseById(request.warehouse());

        if (storageLocationRepository.existsByWarehouseIdAndCode(
                request.warehouse(), request.code())
        ) {
            throw new BusinessRuleViolationException(
                    "Location with code '" + request.code() +
                            "' already exists in this warehouse");
        }

        StorageLocation location = StorageLocation.create(
                warehouse, request.code(), request.zone(),
                request.maxCapacity(), 0);

        return storageLocationMapper.toDto(storageLocationRepository.save(location));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StorageLocationResponse> getByWarehouse(UUID warehouseId) {
        return storageLocationMapper.toDtoList(
                storageLocationRepository.findByWarehouseId(warehouseId));
    }

    @Override
    @Transactional(readOnly = true)
    public StorageLocationResponse getLocationById(UUID id) {

        return storageLocationMapper.toDto(
                storageLocationRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Storage location not found with id: " + id)));
    }
}