package org.example.warehouseinventory.warehouse.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.warehouseinventory.shared.domain.AuditableEntity;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "warehouse")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("active = true")
@AllArgsConstructor

public class Warehouse extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Boolean active;

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public void rename(String name) {
        this.name = name;
    }

    public void relocate(String address) {
        this.address = address;
    }

    public static Warehouse create(String name, String address) {
        Warehouse warehouse = new Warehouse();
        warehouse.name = name;
        warehouse.address = address;
        warehouse.active = true;
        return warehouse;
    }
}
