package org.example.warehouseinventory.warehouse.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.warehouseinventory.shared.domain.enums.AssignmentStrategy;

import java.util.UUID;

@Entity
@Table(name = "warehouse_policy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WarehousePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "warehouse_id", nullable = false, unique = true)
    private UUID warehouse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStrategy strategyName;

    public static WarehousePolicy create(UUID warehouse, AssignmentStrategy strategyName) {

        WarehousePolicy policy = new WarehousePolicy();
        policy.warehouse = warehouse;
        policy.strategyName = strategyName;

        return policy;
    }

    public void updateStrategy(AssignmentStrategy strategyName) {
        this.strategyName = strategyName;
    }
}