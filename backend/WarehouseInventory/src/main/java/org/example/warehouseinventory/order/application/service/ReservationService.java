package org.example.warehouseinventory.order.application.service;

import org.example.warehouseinventory.order.domain.dto.request.ReservationRequest;
import org.example.warehouseinventory.order.domain.dto.response.ReservationResponse;

import java.util.UUID;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest req);
    ReservationResponse confirmReservation(UUID id);
    ReservationResponse releaseReservation(UUID id);
    void releaseExpiredReservations();
}
