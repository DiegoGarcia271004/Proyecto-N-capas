package org.example.warehouseinventory.reporting.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.warehouseinventory.shared.domain.AuditableEntity;

import java.util.UUID;

@Entity
@Table(name = "reorder_suggestion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReorderSuggestion extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID product;

    @Column(nullable = false)
    private UUID warehouse;

    @Column(nullable = false)
    private String productSku;

    @Column(nullable = false)
    private Integer currentStock;

    @Column(nullable = false)
    private Integer reorderPoint;

    @Column(nullable = false)
    private Integer suggestedQuantity;

    @Column(nullable = false)
    private Double avgDailyConsumption;

    @Column(nullable = false)
    private Boolean attended;

    public static ReorderSuggestion create(UUID product, UUID warehouse, String productSku, Integer currentStock,
                                           Integer reorderPoint, Integer suggestedQuantity, Double avgDailyConsumption) {

        ReorderSuggestion reorder = new ReorderSuggestion();

        reorder.product = product;
        reorder.warehouse = warehouse;
        reorder.productSku = productSku;
        reorder.currentStock = currentStock;
        reorder.reorderPoint = reorderPoint;
        reorder.suggestedQuantity = suggestedQuantity;
        reorder.avgDailyConsumption = avgDailyConsumption;
        reorder.attended = false;

        return reorder;
    }

    public void markAsAttended() {
        this.attended = true;
    }

    public void update(Integer currentStock, Integer suggestedQuantity, Double avgDailyConsumption) {

        this.currentStock = currentStock;
        this.suggestedQuantity = suggestedQuantity;
        this.avgDailyConsumption = avgDailyConsumption;
    }
}