package org.example.warehouseinventory.catalog.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.warehouseinventory.shared.domain.AuditableEntity;
import org.example.warehouseinventory.shared.domain.Dimensions;
import org.example.warehouseinventory.shared.domain.Weight;
import org.example.warehouseinventory.shared.domain.enums.ProductCategory;
import org.example.warehouseinventory.shared.domain.enums.StorageRequirement;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLRestriction("active = true")
public class Product extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(unique = true, nullable = false, name = "sku")
    private String sku;

    @Column(name = "product_name")
    private String name;

    @Embedded
    private Dimensions dimensions;

    @Embedded
    private Weight weight;

    private Integer minStockLevel; //helps to make an alert for restock
    private Integer reorderPoint; //help with ROP

    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    @Enumerated(EnumType.STRING)
    private StorageRequirement storageRequirement;

    @Column(nullable = false)
    private Integer leadTimeDays;

    @Column(nullable = false)
    private Integer safetyStock;

    private Boolean active;

    public void deactivate() {
        active = false;
    }

    public void activate() {
        active = true;
    }

    public static Product create(String sku, String name, Dimensions dimensions, Weight weight, Integer minStockLevel, Integer reorderPoint,
                                 ProductCategory productCategory, StorageRequirement storageRequirement, Integer leadTimeDays, Integer safetyStock) {
        Product product = new Product();
        product.sku = sku;
        product.name = name;
        product.productCategory = productCategory;
        product.storageRequirement = storageRequirement;
        product.minStockLevel = minStockLevel;
        product.reorderPoint = reorderPoint;
        product.weight = weight;
        product.active = true;
        product.dimensions = dimensions;
        product.leadTimeDays = leadTimeDays;
        product.safetyStock = safetyStock;
        return product;
    }

    public void update(String sku, String name, ProductCategory category,
                       StorageRequirement storageRequirement, Integer minStockLevel,
                       Integer reorderPoint, Weight weight, Dimensions dimensions,
                       Integer leadTimeDays, Integer safetyStock) {
        this.sku = sku;
        this.name = name;
        this.productCategory = category;
        this.storageRequirement = storageRequirement;
        this.minStockLevel = minStockLevel;
        this.reorderPoint = reorderPoint;
        this.weight = weight;
        this.dimensions = dimensions;
        this.leadTimeDays = leadTimeDays;
        this.safetyStock = safetyStock;
    }
}
