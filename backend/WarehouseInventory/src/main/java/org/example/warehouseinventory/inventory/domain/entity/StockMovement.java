package org.example.warehouseinventory.inventory.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.warehouseinventory.shared.domain.AuditableEntity;
import org.example.warehouseinventory.shared.domain.enums.MovementType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_movement")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class StockMovement extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot", nullable = false)
    private Lot lot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    @Column(nullable = false)
    private Integer quantity;

    private String notes;

}