package org.example.warehouseinventory.warehouse.infrastructure;

import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    boolean existsByName(String name);

    @Query(value = "SELECT * FROM Warehouse w WHERE w.id = :id ", nativeQuery = true)
    Optional<Warehouse> findByIdIncludingInactive(@Param("id") UUID id);
}