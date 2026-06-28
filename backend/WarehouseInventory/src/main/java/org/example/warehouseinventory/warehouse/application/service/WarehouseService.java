package org.example.warehouseinventory.warehouse.application.service;

import org.example.warehouseinventory.warehouse.domain.dto.request.CreateWarehouseRequest;
import org.example.warehouseinventory.warehouse.domain.dto.request.UpdateWarehouseRequest;
import org.example.warehouseinventory.warehouse.domain.dto.response.WarehouseResponse;
import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;

import java.util.List;
import java.util.UUID;

public interface WarehouseService {

    Warehouse getWarehouseById(UUID id);
    WarehouseResponse create(CreateWarehouseRequest request);
    WarehouseResponse update(UUID id, UpdateWarehouseRequest request);
    WarehouseResponse deactivate(UUID id);
    WarehouseResponse activate(UUID id);
    List<WarehouseResponse> getAll();
    WarehouseResponse getById(UUID id);
}