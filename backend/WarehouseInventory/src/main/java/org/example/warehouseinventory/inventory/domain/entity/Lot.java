package org.example.warehouseinventory.inventory.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.warehouseinventory.catalog.domain.entity.Product;
import org.example.warehouseinventory.shared.domain.AuditableEntity;
import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;
import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lot")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Lot extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_location", nullable = false)
    private StorageLocation storageLocation;

    @Column(nullable = false)
    private String lotNumber;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer availableQuantity;

    private LocalDate expirationDate;

    @Column(nullable = false)
    private LocalDateTime receivedAt;

    public void consumeUnits(int units) {

        if (units > this.availableQuantity)
            throw new IllegalArgumentException("Cannot consume more units than available.");

        this.availableQuantity -= units;
    }

}