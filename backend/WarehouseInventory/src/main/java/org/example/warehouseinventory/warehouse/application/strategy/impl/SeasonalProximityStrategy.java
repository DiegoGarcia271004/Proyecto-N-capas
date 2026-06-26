package org.example.warehouseinventory.warehouse.application.strategy.impl;

import org.example.warehouseinventory.shared.domain.enums.AssignmentStrategy;
import org.example.warehouseinventory.shared.domain.enums.StorageRequirement;
import org.example.warehouseinventory.warehouse.application.strategy.LocationAssignmentStrategy;
import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class SeasonalProximityStrategy implements LocationAssignmentStrategy {

    private static final String PACKAGING_ZONE = "PACKAGING";
    private static final String STANDARD_ZONE = "STANDARD";

    @Override
    public AssignmentStrategy strategyName() {
        return AssignmentStrategy.SEASONAL_PROXIMITY;
    }

    @Override
    public StorageLocation assign(List<StorageLocation> availableLocations, StorageRequirement requirement) {

        int month = LocalDate.now().getMonthValue();
        boolean isChristmasSeason = month == 11 || month == 12;
        String targetZone = isChristmasSeason ? PACKAGING_ZONE : STANDARD_ZONE;

        return availableLocations.stream()
                .filter(loc -> loc.getZone().equalsIgnoreCase(targetZone))
                .findFirst()
                .orElse(availableLocations.stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No available locations")));
    }
}