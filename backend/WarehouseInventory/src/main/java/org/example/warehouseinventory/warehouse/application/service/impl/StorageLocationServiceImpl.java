package org.example.warehouseinventory.warehouse.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.shared.domain.enums.AssignmentStrategy;
import org.example.warehouseinventory.shared.domain.enums.StorageRequirement;
import org.example.warehouseinventory.warehouse.application.service.StorageLocationService;
import org.example.warehouseinventory.warehouse.application.service.WarehousePolicyService;
import org.example.warehouseinventory.warehouse.application.strategy.LocationAssignmentStrategy;
import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;
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

//    y ademas dime, en el caso de storage location service debo eliminar el metodo que estaba antes de findAvailableStorageLocation a este
//    nuevo que creaste de findAvailableLocation, literalmente es una refactorizacion de ese metodo
//
//    @Override
//    @Transactional(readOnly = true)
//    public StorageLocation findAvailableStorageLocation(UUID warehouse, Integer quantity) {
//
//        return storageLocationRepository
//                .findAvailableByCapacity(warehouse, quantity)
//                .stream()
//                .findFirst()
//                .orElseThrow(() -> new NoAvailableStorageLocationException(
//                        warehouse, quantity,
//                        storageLocationRepository.getCapacityAvailableByWarehouse(warehouse)
//                ));
//    }

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
}