package org.example.warehouseinventory.order.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.order.application.service.ReservationService;
import org.example.warehouseinventory.order.domain.dto.request.ReservationRequest;
import org.example.warehouseinventory.shared.api.BaseController;
import org.example.warehouseinventory.shared.domain.GeneralResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController extends BaseController {

    private final ReservationService reservationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'OPERATOR')")
    public ResponseEntity<GeneralResponse> createReservation(
            @Valid @RequestBody ReservationRequest req
            ) {
        return buildResponse(
                "Reservation created successfully",
                HttpStatus.CREATED,
                reservationService.createReservation(req)
        );

    }

    @PutMapping("/confirm/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'OPERATOR')")
    public ResponseEntity<GeneralResponse> confirmReservation(@PathVariable UUID id) {
        return buildResponse(
                "Reservation confirmed successfully",
                HttpStatus.OK,
                reservationService.confirmReservation(id)
        );
    }

    @PutMapping("/release/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'OPERATOR')")
    public ResponseEntity<GeneralResponse> releaseReservation(@PathVariable UUID id) {
        return buildResponse(
                "Reservation released successfully",
                HttpStatus.OK,
                reservationService.releaseReservation(id)
        );
    }
}
