package org.example.warehouseinventory.inventory.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.warehouseinventory.catalog.domain.entity.Product;
import org.example.warehouseinventory.shared.domain.AuditableEntity;
import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Entity
@Table(
        name = "product_cost",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"product_id", "warehouse_id"}
        )
)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCost extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal averageCost;

    @Column(nullable = false)
    private Integer totalQuantity;

    public void recalculate(BigDecimal newCost, Integer newQuantity) {

        if (newQuantity <= 0) return;

        BigDecimal currentTotal = this.averageCost.multiply(
                BigDecimal.valueOf(this.totalQuantity)
        );
        BigDecimal newTotal = newCost.multiply(BigDecimal.valueOf(newQuantity));

        this.totalQuantity += newQuantity;
        this.averageCost = currentTotal.add(newTotal)
                .divide(BigDecimal.valueOf(this.totalQuantity), 2, RoundingMode.HALF_UP);
    }

    public static ProductCost create(Product product, Warehouse warehouse, BigDecimal averageCost, Integer totalQuantity) {
        ProductCost productCost = new ProductCost();
        productCost.product = product;
        productCost.warehouse = warehouse;
        productCost.averageCost = averageCost;
        productCost.totalQuantity = totalQuantity;
        return productCost;
    }
}