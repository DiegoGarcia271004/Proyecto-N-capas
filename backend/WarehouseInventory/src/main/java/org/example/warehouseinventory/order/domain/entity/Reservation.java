package org.example.warehouseinventory.order.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.warehouseinventory.catalog.domain.entity.Product;
import org.example.warehouseinventory.order.domain.enums.ReservationStatus;
import org.example.warehouseinventory.shared.domain.AuditableEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservation")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reservation extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /*
    * @ManyToOne(fetch = FetchType.LAZY, optional = false)
    * @JoinColumn(name = "warehouse_id", nullable = false)
    * private Warehouse warehouse;
    * */

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    public void release() {
        this.status = ReservationStatus.RELEASED;
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }
}
