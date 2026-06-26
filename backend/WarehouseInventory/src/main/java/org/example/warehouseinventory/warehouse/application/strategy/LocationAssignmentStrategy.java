package org.example.warehouseinventory.warehouse.application.strategy;

import org.example.warehouseinventory.shared.domain.enums.AssignmentStrategy;
import org.example.warehouseinventory.shared.domain.enums.StorageRequirement;
import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;

import java.util.List;

public interface LocationAssignmentStrategy {

    AssignmentStrategy strategyName();
    StorageLocation assign(List<StorageLocation> availableLocations, StorageRequirement requirement);
}