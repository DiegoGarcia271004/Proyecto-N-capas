package org.example.warehouseinventory.warehouse.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.shared.domain.enums.AssignmentStrategy;
import org.example.warehouseinventory.warehouse.application.service.WarehousePolicyService;
import org.example.warehouseinventory.warehouse.domain.entity.WarehousePolicy;
import org.example.warehouseinventory.warehouse.infrastructure.WarehousePolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehousePolicyServiceImpl implements WarehousePolicyService {

    private final WarehousePolicyRepository warehousePolicyRepository;

    @Override
    @Transactional(readOnly = true)
    public WarehousePolicy getOrDefault(UUID warehouse) {

        return warehousePolicyRepository.findByWarehouse(warehouse)
                .orElseGet(() -> WarehousePolicy.create(warehouse, AssignmentStrategy.RANDOM));
    }

    @Override
    @Transactional
    public WarehousePolicy setStrategy(UUID warehouse, AssignmentStrategy strategy) {

        WarehousePolicy policy = warehousePolicyRepository
                .findByWarehouse(warehouse)
                .orElseGet(() -> WarehousePolicy.create(warehouse, strategy));

        policy.updateStrategy(strategy);

        return warehousePolicyRepository.save(policy);
    }

//    dime, en que momento es que se le asigna una politica a un warehouse, bueno supongo que eso ira en la parte al terminar modulo warehouse,
//    o simplemente se asumira que no tiene y que el update implementado al mismo tiempo funcione como create, o que al crear un warehouse,
//    aunque no se en que parte esta o debe ir eso, se coloque como random strategy por default, deberia de dar opciones al usuario
}