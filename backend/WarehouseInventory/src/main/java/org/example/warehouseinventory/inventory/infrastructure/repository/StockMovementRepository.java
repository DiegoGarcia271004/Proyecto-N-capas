package org.example.warehouseinventory.inventory.infrastructure.repository;

import org.example.warehouseinventory.inventory.domain.entity.StockMovement;
import org.example.warehouseinventory.inventory.infrastructure.repository.projection.ExitSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    List<StockMovement> findByLotId(UUID lotId);

    @Query(value = """
            SELECT l.product_id      AS productId,
                   l.warehouse_id    AS warehouseId,
                   CAST(SUM(sm.quantity) AS INTEGER) AS totalExitQuantity
            FROM stock_movement sm
            JOIN lot l ON sm.lot_id = l.id
            WHERE sm.type = 'EXIT'
              AND sm.created_at BETWEEN :from AND :to
            GROUP BY l.product_id, l.warehouse_id
            """, nativeQuery = true)
    List<ExitSummaryProjection> sumExitByProductAndWarehouse(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query(value = """
        SELECT COALESCE(SUM(sm.quantity), 0)
        FROM stock_movement sm
        INNER JOIN lot l ON sm.lot = l.id
        WHERE l.product_id = :product
        AND l.warehouse = :warehouse
        AND sm.type = 'EXIT'
        AND sm.created_at >= NOW() - INTERVAL '90 days'
    """, nativeQuery = true)
    Integer getTotalExitQuantityLast90Days(
            @Param("product") UUID product,
            @Param("warehouse") UUID warehouse
    );
}