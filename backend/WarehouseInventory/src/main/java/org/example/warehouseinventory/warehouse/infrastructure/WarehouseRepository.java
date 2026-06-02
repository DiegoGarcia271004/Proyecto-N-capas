package org.example.warehouseinventory.warehouse.infrastructure;

import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    boolean existsByName(String name);
}