package org.example.warehouseinventory.inventory.infrastructure.repository;

import org.example.warehouseinventory.inventory.domain.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LotRepository extends JpaRepository<Lot, UUID> {

    @Query(value = "SELECT * FROM lot " +
            "WHERE product = :productId " +
            "AND warehouse = :warehouseId " +
            "AND available_quantity > 0 " +
            "ORDER BY expiration_date ASC NULLS LAST, received_at ASC",
            nativeQuery = true)
    List<Lot> findAvailableLotsFifo(@Param("productId") UUID productId,
                                    @Param("warehouseId") UUID warehouseId);
}