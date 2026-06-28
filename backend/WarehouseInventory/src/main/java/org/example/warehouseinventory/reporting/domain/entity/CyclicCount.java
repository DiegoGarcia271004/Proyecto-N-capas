package org.example.warehouseinventory.reporting.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.warehouseinventory.shared.domain.AuditableEntity;
import org.example.warehouseinventory.shared.domain.enums.CountStatus;

import java.util.UUID;

@Entity
@Table(name = "cyclic_count")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CyclicCount extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID lot;

    @Column(nullable = false)
    private Integer systemQuantity;

    private Integer physicalQuantity;

    private Integer discrepancy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CountStatus status;

    public static CyclicCount create(UUID lot, Integer systemQuantity) {

        CyclicCount cc = new CyclicCount();

        cc.lot = lot;
        cc.systemQuantity = systemQuantity;
        cc.status = CountStatus.PENDING;

        return cc;
    }

    public void start() {

        if (this.status != CountStatus.PENDING)
            throw new IllegalStateException("Cyclic count can only be started from PENDING status");

        this.status = CountStatus.IN_PROGRESS;
    }

    public void adjust(Integer physicalQuantity) {

        if (this.status != CountStatus.IN_PROGRESS)
            throw new IllegalStateException("Cyclic count can only be adjusted from IN_PROGRESS status");

        this.physicalQuantity = physicalQuantity;
        this.discrepancy = physicalQuantity - this.systemQuantity;
        this.status = CountStatus.ADJUSTED;
    }
}