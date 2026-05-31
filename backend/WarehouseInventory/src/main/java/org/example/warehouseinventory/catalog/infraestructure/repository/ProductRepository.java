package org.example.warehouseinventory.catalog.infraestructure.repository;

import org.example.warehouseinventory.catalog.domain.entity.Product;
import org.example.warehouseinventory.shared.domain.enums.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    @Query(value = "SELECT * FROM product WHERE active = false AND active = true" ,nativeQuery = true)
    List<Product> findAll();
}
