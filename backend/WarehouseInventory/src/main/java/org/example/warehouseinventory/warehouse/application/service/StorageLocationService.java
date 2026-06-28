package org.example.warehouseinventory.warehouse.application.service;

import org.example.warehouseinventory.shared.domain.enums.StorageRequirement;
import org.example.warehouseinventory.warehouse.domain.dto.request.CreateStorageLocationRequest;
import org.example.warehouseinventory.warehouse.domain.dto.response.StorageLocationResponse;
import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;

import java.util.List;
import java.util.UUID;

public interface StorageLocationService {

    void updateOccupancy(StorageLocation location, int units);
    void releaseOccupancy(StorageLocation location, int units);
    StorageLocation findAvailableStorageLocation(UUID warehouse, Integer quantity, StorageRequirement requirement);
    StorageLocationResponse createLocation(CreateStorageLocationRequest request);
    List<StorageLocationResponse> getByWarehouse(UUID warehouseId);
    StorageLocationResponse getLocationById(UUID id);
}