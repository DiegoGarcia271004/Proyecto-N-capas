package org.example.warehouseinventory.warehouse.application.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.shared.api.exception.BusinessRuleViolationException;
import org.example.warehouseinventory.shared.api.exception.ResourceNotFoundException;
import org.example.warehouseinventory.warehouse.api.mapper.WarehouseMapper;
import org.example.warehouseinventory.warehouse.application.service.WarehouseService;
import org.example.warehouseinventory.warehouse.domain.dto.request.CreateWarehouseRequest;
import org.example.warehouseinventory.warehouse.domain.dto.request.UpdateWarehouseRequest;
import org.example.warehouseinventory.warehouse.domain.dto.response.WarehouseResponse;
import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;
import org.example.warehouseinventory.warehouse.infrastructure.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Warehouse getWarehouseById(UUID id) {

        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Warehouse not found with ID: " + id
                ));
    }

    @Override
    @Transactional
    public WarehouseResponse create(CreateWarehouseRequest request) {

        if (warehouseRepository.existsByName(request.name()))
            throw new BusinessRuleViolationException("Warehouse with name '" + request.name() + "' already exists");

        Warehouse warehouse = Warehouse.create(request.name(), request.address());
        return warehouseMapper.toDto(warehouseRepository.save(warehouse));
    }

    @Override
    @Transactional
    public WarehouseResponse update(UUID id, UpdateWarehouseRequest request) {

        Warehouse warehouse = getWarehouseById(id);
        warehouse.rename(request.name());
        warehouse.relocate(request.address());

        return warehouseMapper.toDto(warehouseRepository.save(warehouse));
    }

    @Override
    @Transactional
    public WarehouseResponse deactivate(UUID id) {

        Warehouse warehouse = getWarehouseById(id);
        warehouse.deactivate();

        return warehouseMapper.toDto(warehouseRepository.save(warehouse));
    }

    @Override
    @Transactional
    public WarehouseResponse activate(UUID id) {

        Warehouse warehouse = warehouseRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Warehouse not found with id: " + id));

        warehouse.activate();
        return warehouseMapper.toDto(warehouseRepository.save(warehouse));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseResponse> getAll() {
        return warehouseMapper.toDtoList(warehouseRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseResponse getById(UUID id) {
        return warehouseMapper.toDto(getWarehouseById(id));
    }

}