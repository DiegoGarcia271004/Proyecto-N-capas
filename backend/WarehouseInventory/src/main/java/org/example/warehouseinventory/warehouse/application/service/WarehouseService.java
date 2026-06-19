package org.example.warehouseinventory.warehouse.application.service;

import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;

import java.util.UUID;

public interface WarehouseService {

    Warehouse getWarehouseById(UUID id);
}