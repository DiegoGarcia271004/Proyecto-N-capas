package org.example.warehouseinventory.warehouse.infrastructure;

import org.example.warehouseinventory.warehouse.domain.entity.WarehousePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehousePolicyRepository extends JpaRepository<WarehousePolicy, UUID> {
    Optional<WarehousePolicy> findByWarehouse(UUID warehouse);
}