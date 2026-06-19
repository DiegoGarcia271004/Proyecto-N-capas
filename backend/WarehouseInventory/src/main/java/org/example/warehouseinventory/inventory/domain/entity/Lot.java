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
    @JoinColumn(name = "product_id", nullable = false)
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

    public void releaseUnits(int units) {
        if (this.availableQuantity + units > this.quantity)
            throw new IllegalArgumentException("Cannot release more units than were consumed");

        this.availableQuantity += units;
    }

    public void confirmConsumption(int units) {
        int reserved = this.quantity - this.availableQuantity;
        if (units > reserved)
            throw new IllegalArgumentException("Cannot confirm more units than reserved");
        this.quantity -= units;
    }

    public static Lot create(Product product, Warehouse warehouse, StorageLocation location,
                             String lotNumber, Integer quantity, LocalDate expirationDate) {
        Lot lot = new Lot();
        lot.product = product;
        lot.warehouse = warehouse;
        lot.storageLocation = location;
        lot.lotNumber = lotNumber;
        lot.quantity = quantity;
        lot.availableQuantity = quantity;
        lot.expirationDate = expirationDate;
        lot.receivedAt = LocalDateTime.now();
        return lot;
    }

}