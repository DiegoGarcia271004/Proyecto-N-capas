package org.example.warehouseinventory.order.infraestructure.repository;

import org.example.warehouseinventory.order.domain.entity.Reservation;
import org.example.warehouseinventory.order.domain.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByProduct_Id(UUID productId);

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByStatusAndExpiresAtBefore(ReservationStatus status, LocalDateTime dateTime);
}
