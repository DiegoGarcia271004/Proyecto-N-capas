package org.example.warehouseinventory.catalog.infraestructure.repository;

import org.example.warehouseinventory.catalog.domain.dto.response.LowStockProjection;
import org.example.warehouseinventory.catalog.domain.entity.Product;
import org.example.warehouseinventory.shared.domain.enums.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Product> findByProductCategory(ProductCategory category);

    @Query(value = "SELECT * FROM product WHERE id = :id", nativeQuery = true)
    Optional<Product> findByIdIncludingInactive(@Param("id") UUID id);

    @Query(value = "SELECT * FROM product", nativeQuery = true)
    List<Product> findAllIncludingInactive();

    @Query(value = "SELECT * FROM product WHERE sku = :sku", nativeQuery = true)
    Optional<Product> findBySkuIncludingInactive(@Param("sku") String sku);

    @Query(value = """
        SELECT p.id as productId, p.sku as sku, p.product_name as name,
               COALESCE(SUM(l.available_quantity), 0) as currentStock,
               p.min_stock_level as minStockLevel
        FROM product p
        LEFT JOIN lot l ON l.product_id = p.id
            AND l.available_quantity > 0
            AND (l.expiration_date IS NULL OR l.expiration_date >= CURRENT_DATE)
        WHERE p.active = true
        GROUP BY p.id, p.sku, p.product_name, p.min_stock_level
        HAVING COALESCE(SUM(l.available_quantity), 0) < p.min_stock_level
        """, nativeQuery = true)
    List<LowStockProjection> findProductsBelowMinStock();

}
