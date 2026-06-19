package org.example.warehouseinventory.order.infrastructure.repository;

import org.example.warehouseinventory.order.domain.entity.Reservation;
import org.example.warehouseinventory.order.domain.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByProductIdAndStatus(UUID productId, ReservationStatus status);

    @Query(value = "SELECT * FROM reservation WHERE status = 'ACTIVE' AND expires_at < NOW()", nativeQuery = true)
    List<Reservation> findExpiredReservations();

}
