package org.example.warehouseinventory.order.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.warehouseinventory.inventory.domain.entity.Lot;

import java.util.UUID;

@Entity
@Table(name = "reservation_lot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationLot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lot_id", nullable = false)
    private Lot lot;

    @Column(nullable = false)
    private Integer quantity;

    public static ReservationLot create(Reservation reservation, Lot lot, Integer quantity) {
        ReservationLot rl = new ReservationLot();
        rl.reservation = reservation;
        rl.lot = lot;
        rl.quantity = quantity;
        return rl;
    }

}
