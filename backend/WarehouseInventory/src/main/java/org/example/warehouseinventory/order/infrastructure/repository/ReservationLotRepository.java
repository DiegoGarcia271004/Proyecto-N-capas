package org.example.warehouseinventory.order.infrastructure.repository;

import org.example.warehouseinventory.order.domain.entity.ReservationLot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReservationLotRepository extends JpaRepository<ReservationLot, UUID> {
    List<ReservationLot> findByReservationId(UUID reservationId);
}
