package org.example.warehouseinventory.warehouse.application.service;

import org.example.warehouseinventory.shared.domain.enums.AssignmentStrategy;
import org.example.warehouseinventory.warehouse.domain.entity.WarehousePolicy;

import java.util.UUID;

public interface WarehousePolicyService {
    WarehousePolicy getOrDefault(UUID warehouse);
    WarehousePolicy setStrategy(UUID warehouse, AssignmentStrategy strategy);
}