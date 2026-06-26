package org.example.warehouseinventory.warehouse.application.strategy.impl;

import org.example.warehouseinventory.shared.domain.enums.AssignmentStrategy;
import org.example.warehouseinventory.shared.domain.enums.StorageRequirement;
import org.example.warehouseinventory.warehouse.application.strategy.LocationAssignmentStrategy;
import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class AisleFillStrategy implements LocationAssignmentStrategy {

    @Override
    public AssignmentStrategy strategyName() {
        return AssignmentStrategy.AISLE_FILL;
    }

    @Override
    public StorageLocation assign(List<StorageLocation> availableLocations, StorageRequirement requirement) {

        return availableLocations.stream()
                .min(Comparator.comparing(StorageLocation::getCode))
                .orElseThrow(() -> new IllegalStateException("No available locations"));
    }
}