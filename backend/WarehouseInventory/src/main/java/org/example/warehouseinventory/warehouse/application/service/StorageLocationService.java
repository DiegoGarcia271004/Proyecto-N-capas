package org.example.warehouseinventory.warehouse.application.service;

import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;

import java.util.UUID;

public interface StorageLocationService {

    StorageLocation findAvailableStorageLocation(UUID warehouse, Integer quantity);
    void updateOccupancy(StorageLocation location, int units);
    void releaseOccupancy(StorageLocation location, int units);
}