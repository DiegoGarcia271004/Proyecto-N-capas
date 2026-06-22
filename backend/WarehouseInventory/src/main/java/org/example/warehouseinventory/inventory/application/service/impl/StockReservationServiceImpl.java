package org.example.warehouseinventory.inventory.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.inventory.application.service.StockReservationService;
import org.example.warehouseinventory.inventory.domain.dto.LotReservationDetail;
import org.example.warehouseinventory.inventory.domain.entity.Lot;
import org.example.warehouseinventory.inventory.domain.entity.StockMovement;
import org.example.warehouseinventory.inventory.domain.exception.InsufficientStockException;
import org.example.warehouseinventory.inventory.infrastructure.repository.LotRepository;
import org.example.warehouseinventory.inventory.infrastructure.repository.StockMovementRepository;
import org.example.warehouseinventory.shared.api.exception.ResourceNotFoundException;
import org.example.warehouseinventory.shared.domain.enums.MovementType;
import org.example.warehouseinventory.warehouse.application.service.StorageLocationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockReservationServiceImpl implements StockReservationService {

    private final LotRepository lotRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StorageLocationService storageLocationService;


    @Override
    @Transactional
    public List<LotReservationDetail> reserveStock(UUID productId, UUID warehouseId, Integer quantity) {
        List<Lot> lots = lotRepository.findAvailableLotsFifo(productId, warehouseId);

        int totalAvailable = lots.stream().mapToInt(Lot::getAvailableQuantity).sum();
        if (totalAvailable < quantity) {
            throw new InsufficientStockException(productId.toString(), quantity, totalAvailable);
        }

        List<LotReservationDetail> details = new ArrayList<>();
        int remaining = quantity;

        for (Lot lot : lots) {
            if (remaining == 0) break;
            int toReserve = Math.min(remaining, lot.getAvailableQuantity());
            lot.consumeUnits(toReserve);
            lotRepository.save(lot);

            details.add(new LotReservationDetail(lot.getId(), toReserve));
            remaining -= toReserve;
        }

        return details;

    }

    @Override
    @Transactional
    public void releaseStock(List<LotReservationDetail> details) {
        details.forEach(detail -> {
            Lot lot = lotRepository.findById(detail.lotId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Lot not found with id: " + detail.lotId()));
            lot.releaseUnits(detail.quantity());
            lotRepository.save(lot);
        });
    }

    @Override
    @Transactional
    public void confirmStock(List<LotReservationDetail> details) {
        details.forEach(detail -> {
            Lot lot = lotRepository.findById(detail.lotId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Lot not found with id: " + detail.lotId()));

            lot.confirmConsumption(detail.quantity());
            lotRepository.save(lot);

            storageLocationService.releaseOccupancy(lot.getStorageLocation(), detail.quantity());

            StockMovement movement = StockMovement.create(
                    lot, MovementType.EXIT, detail.quantity(), "");
            stockMovementRepository.save(movement);
        });
    }

    @Override
    public Lot getLotEntityById(UUID id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lot not found with id " + id));
    }
}
