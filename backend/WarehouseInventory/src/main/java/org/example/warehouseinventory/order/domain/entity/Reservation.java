package org.example.warehouseinventory.order.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.warehouseinventory.catalog.domain.entity.Product;
import org.example.warehouseinventory.order.domain.enums.ReservationStatus;
import org.example.warehouseinventory.shared.domain.AuditableEntity;
import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservation")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;


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

    public static Reservation create(Product product, Warehouse warehouse, Integer quantity, LocalDateTime expiresAt) {
        Reservation reservation = new Reservation();
        reservation.product = product;
        reservation.warehouse = warehouse;
        reservation.quantity = quantity;
        reservation.status = ReservationStatus.ACTIVE;
        reservation.expiresAt = expiresAt;
        return reservation;
    }
}
