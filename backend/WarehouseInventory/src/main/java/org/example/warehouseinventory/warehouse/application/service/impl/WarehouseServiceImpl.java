package org.example.warehouseinventory.warehouse.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.shared.api.exception.ResourceNotFoundException;
import org.example.warehouseinventory.warehouse.application.service.WarehouseService;
import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;
import org.example.warehouseinventory.warehouse.infrastructure.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional(readOnly = true)
    public Warehouse getWarehouseById(UUID id) {

        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Warehouse not found with ID: " + id
                ));
    }
}