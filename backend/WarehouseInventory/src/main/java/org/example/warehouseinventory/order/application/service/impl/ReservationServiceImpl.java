package org.example.warehouseinventory.order.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.catalog.application.service.ProductService;
import org.example.warehouseinventory.catalog.domain.entity.Product;
import org.example.warehouseinventory.inventory.application.service.StockReservationService;
import org.example.warehouseinventory.inventory.domain.dto.LotReservationDetail;
import org.example.warehouseinventory.inventory.domain.entity.Lot;
import org.example.warehouseinventory.order.api.mapper.ReservationMapper;
import org.example.warehouseinventory.order.application.service.ReservationService;
import org.example.warehouseinventory.order.domain.dto.request.ReservationRequest;
import org.example.warehouseinventory.order.domain.dto.response.ReservationResponse;
import org.example.warehouseinventory.order.domain.entity.Reservation;
import org.example.warehouseinventory.order.domain.entity.ReservationLot;
import org.example.warehouseinventory.order.domain.enums.ReservationStatus;
import org.example.warehouseinventory.order.infrastructure.repository.ReservationLotRepository;
import org.example.warehouseinventory.order.infrastructure.repository.ReservationRepository;
import org.example.warehouseinventory.shared.api.exception.BusinessRuleViolationException;
import org.example.warehouseinventory.shared.api.exception.ResourceNotFoundException;
import org.example.warehouseinventory.warehouse.application.service.WarehouseService;
import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationLotRepository reservationLotRepository;
    private final WarehouseService warehouseService;
    private final ProductService productService;
    private final StockReservationService stockReservationService;
    private final ReservationMapper mapper;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest req) {
        Product product = productService.getProductEntityById(req.productId());
        Warehouse warehouse = warehouseService.getWarehouseById(req.warehouseId());

        List<LotReservationDetail> details = stockReservationService.reserveStock(
                req.productId(), req.warehouseId(), req.quantity()
        );

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(req.expirationMinutes());
        Reservation reservation = Reservation.create(product, warehouse, req.quantity(), expiresAt);
        reservationRepository.save(reservation);

        details.forEach(detail -> {
            Lot lot = stockReservationService.getLotEntityById(detail.lotId());
            ReservationLot reservationLot = ReservationLot.create(reservation, lot, detail.quantity());
            reservationLotRepository.save(reservationLot);
        });

        return mapper.toDto(reservation);
    }

    @Override
    @Transactional
    public ReservationResponse confirmReservation(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new BusinessRuleViolationException("Only ACTIVE reservations can be confirmed");
        }

        List<LotReservationDetail> details = toDetails(reservation.getId());
        stockReservationService.confirmStock(details);

        reservation.confirm();
        reservationRepository.save(reservation);

        return mapper.toDto(reservation);
    }

    @Override
    @Transactional
    public ReservationResponse releaseReservation(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id:" + id));

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new BusinessRuleViolationException("Only ACTIVE reservations can be released");
        }

        List<LotReservationDetail> details = toDetails(reservation.getId());
        stockReservationService.releaseStock(details);

        reservation.release();
        reservationRepository.save(reservation);

        return mapper.toDto(reservation);
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 60_000)
    public void releaseExpiredReservations() {
        List<Reservation> expired = reservationRepository.findExpiredReservations();
        expired.forEach(reservation -> {
            List<LotReservationDetail> details = toDetails(reservation.getId());
            stockReservationService.releaseStock(details);
            reservation.release();
            reservationRepository.save(reservation);
        });
    }

    private List<LotReservationDetail> toDetails(UUID reservationId) {
        return mapper.toDetails(reservationLotRepository.findByReservationId(reservationId));
    }
}
