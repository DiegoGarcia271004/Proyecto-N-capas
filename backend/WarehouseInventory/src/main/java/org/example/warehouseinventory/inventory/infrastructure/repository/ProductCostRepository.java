package org.example.warehouseinventory.inventory.infrastructure.repository;

import org.example.warehouseinventory.inventory.domain.entity.ProductCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductCostRepository extends JpaRepository<ProductCost, UUID> {

    Optional<ProductCost> findByProductIdAndWarehouseId(UUID product, UUID warehosue);
}