package org.example.warehouseinventory.warehouse.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.warehouse.application.service.StorageLocationService;
import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;
import org.example.warehouseinventory.warehouse.domain.exception.NoAvailableStorageLocationException;
import org.example.warehouseinventory.warehouse.infrastructure.StorageLocationRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageLocationServiceImpl implements StorageLocationService {

    private final StorageLocationRepository storageLocationRepository;

    @Override
    public StorageLocation findAvailableStorageLocation(UUID warehouse, Integer quantity) {

        return storageLocationRepository
                .findAvailableByCapacity(warehouse, quantity)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoAvailableStorageLocationException(
                        warehouse, quantity,
                        storageLocationRepository.getCapacityAvailableByWarehouse(warehouse)
                ));
    }

    @Override
    public void updateOccupancy(StorageLocation location, int units) {
        location.addOccupancy(units);
        storageLocationRepository.save(location);
    }

    @Override
    public void releaseOccupancy(StorageLocation location, int units) {
        location.removeOccupancy(units);
        storageLocationRepository.save(location);
    }
}