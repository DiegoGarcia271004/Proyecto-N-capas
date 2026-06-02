package org.example.warehouseinventory.warehouse.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.warehouseinventory.shared.domain.AuditableEntity;

import java.util.UUID;

@Entity
@Table(name = "storage_location")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class StorageLocation extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String zone;

    @Column(nullable = false)
    private Integer maxCapacity;

    @Column(nullable = false)
    private Integer currentOccupancy;

    @Column(nullable = false)
    private Boolean available;

    public void addOccupancy(int units) {
        this.currentOccupancy += units;
        this.available = this.currentOccupancy < this.maxCapacity;
    }

    public void removeOccupancy(int units) {
        this.currentOccupancy = Math.max(0, this.currentOccupancy - units);
        this.available = this.currentOccupancy < this.maxCapacity;
    }
}