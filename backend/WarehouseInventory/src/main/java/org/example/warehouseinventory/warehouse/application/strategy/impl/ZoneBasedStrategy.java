package org.example.warehouseinventory.warehouse.application.strategy.impl;

import org.example.warehouseinventory.shared.domain.enums.AssignmentStrategy;
import org.example.warehouseinventory.shared.domain.enums.StorageRequirement;
import org.example.warehouseinventory.warehouse.application.strategy.LocationAssignmentStrategy;
import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ZoneBasedStrategy implements LocationAssignmentStrategy {

    @Override
    public AssignmentStrategy strategyName() {
        return AssignmentStrategy.ZONE_BASED;
    }

    @Override
    public StorageLocation assign(List<StorageLocation> availableLocations, StorageRequirement requirement) {

        return availableLocations.stream()
                .filter(loc -> loc.getZone().equalsIgnoreCase(requirement.name()))
                .findFirst()
                .orElse(availableLocations.stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No available locations")));
    }
}