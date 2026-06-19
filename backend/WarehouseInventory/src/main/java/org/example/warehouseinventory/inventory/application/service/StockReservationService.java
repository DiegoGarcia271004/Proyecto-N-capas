package org.example.warehouseinventory.inventory.application.service;

import org.example.warehouseinventory.inventory.domain.dto.LotReservationDetail;
import org.example.warehouseinventory.inventory.domain.entity.Lot;

import java.util.List;
import java.util.UUID;

public interface StockReservationService {
    List<LotReservationDetail> reserveStock(UUID productId, UUID warehouseId, Integer quantity);
    void releaseStock(List<LotReservationDetail> details);
    void confirmStock(List<LotReservationDetail> details);
    Lot getLotEntityById(UUID id);
}
