package org.example.warehouseinventory.warehouse.infrastructure;

import org.example.warehouseinventory.warehouse.domain.StorageLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StorageLocationRepository extends JpaRepository<StorageLocation, UUID> {

    List<StorageLocation> findByWarehouseIdAndAvailableTrue(UUID warehouse);
}