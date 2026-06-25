package org.example.warehouseinventory.inventory.infrastructure.repository;

import org.example.warehouseinventory.catalog.domain.dto.response.ReorderProjection;
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
            "AND (expiration_date IS NULL OR expiration_date >= CURRENT_DATE) " +
            "ORDER BY expiration_date ASC NULLS LAST, received_at ASC",
            nativeQuery = true)
    List<Lot> findAvailableLotsFifo(@Param("productId") UUID productId,
                                    @Param("warehouseId") UUID warehouseId);

    @Query(value = """
        SELECT * FROM lot
        WHERE available_quantity > 0
        AND expiration_date IS NOT NULL
        AND expiration_date < CURRENT_DATE
        """, nativeQuery = true)
    List<Lot> findExpiredLotsWithStock();

    @Query(value = """
        SELECT p.id as productId, p.sku as sku, w.id as warehouseId,
               COALESCE(SUM(l.available_quantity), 0) as currentStock,
               p.reorder_point as reorderPoint,
               p.lead_time_days as leadTimeDays,
               p.safety_stock as safetyStock
        FROM product p
        CROSS JOIN warehouse w
        LEFT JOIN lot l ON l.product_id = p.id
            AND l.warehouse = w.id
            AND l.available_quantity > 0
            AND (l.expiration_date IS NULL OR l.expiration_date >= CURRENT_DATE)
        WHERE p.active = true
        AND w.active = true
        GROUP BY p.id, p.sku, w.id, p.reorder_point, p.lead_time_days, p.safety_stock
        HAVING COALESCE(SUM(l.available_quantity), 0) <= p.reorder_point
        """, nativeQuery = true)
    List<ReorderProjection> findProductsBelowReorderPoint();
}