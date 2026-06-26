package org.example.warehouseinventory.inventory.application.service;

import org.example.warehouseinventory.catalog.domain.dto.response.ReorderProjection;
import org.example.warehouseinventory.inventory.domain.entity.Lot;

import java.util.List;
import java.util.UUID;

public interface LotService {

    List<Lot> findExpiredLotsWithStock();
    List<ReorderProjection> findProductsBelowReorderPoint();
    Lot findById(UUID id);
    void applyDiscrepancy(Lot lot, Integer discrepancy);
}