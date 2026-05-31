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

    private Boolean active;

    public void deactivate() {
        active = false;
    }

    public void activate() {
        active = true;
    }
}
