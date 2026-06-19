package org.example.warehouseinventory.order.api.mapper;

import org.example.warehouseinventory.inventory.domain.dto.LotReservationDetail;
import org.example.warehouseinventory.order.domain.dto.response.ReservationResponse;
import org.example.warehouseinventory.order.domain.entity.Reservation;
import org.example.warehouseinventory.order.domain.entity.ReservationLot;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationMapper {

    public ReservationResponse toDto(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getProduct().getId(),
                reservation.getWarehouse().getId(),
                reservation.getProduct().getSku(),
                reservation.getQuantity(),
                reservation.getStatus(),
                reservation.getExpiresAt(),
                reservation.getExpiresAt()
        );
    }

    public List<ReservationResponse> toDtoList(List<Reservation> reservations) {
        return reservations.stream().map(this::toDto).toList();
    }

    public List<LotReservationDetail> toDetails(List<ReservationLot> reservationLots) {
        return reservationLots.stream()
                .map(rl -> new LotReservationDetail(rl.getLot().getId(), rl.getQuantity()))
                .toList();
    }
}
