package org.example.warehouseinventory.warehouse.application.strategy.impl;

import org.example.warehouseinventory.shared.domain.enums.AssignmentStrategy;
import org.example.warehouseinventory.shared.domain.enums.StorageRequirement;
import org.example.warehouseinventory.warehouse.application.strategy.LocationAssignmentStrategy;
import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class RandomStrategy implements LocationAssignmentStrategy {

    private final Random random = new Random();

    @Override
    public AssignmentStrategy strategyName() {
        return AssignmentStrategy.RANDOM;
    }

    @Override
    public StorageLocation assign(List<StorageLocation> availableLocations, StorageRequirement requirement) {
        return availableLocations.get(random.nextInt(availableLocations.size()));
    }
}