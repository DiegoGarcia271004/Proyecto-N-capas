package org.example.warehouseinventory.warehouse.infrastructure;

import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StorageLocationRepository extends JpaRepository<StorageLocation, UUID> {

    @Query("SELECT sl FROM StorageLocation sl WHERE sl.warehouse.id = :warehouse " +
            "AND sl.available = TRUE " +
            "AND (sl.maxCapacity - sl.currentOccupancy) >= :quantity")
    List<StorageLocation> findAvailableByCapacity(
            @Param("warehouse") UUID warehouse,
            @Param("quantity") Integer quantity
    );

    @Query("SELECT COALESCE(SUM(sl.maxCapacity - sl.currentOccupancy), 0) FROM StorageLocation sl " +
            "WHERE sl.warehouse.id = :warehouse " +
            "AND sl.available = TRUE")
    Integer getCapacityAvailableByWarehouse(@Param("warehouse") UUID warehouse);
}