package org.example.warehouseinventory.inventory.infrastructure.repository;

import org.example.warehouseinventory.inventory.domain.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    List<StockMovement> findByLotId(UUID id);
}