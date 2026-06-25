package org.example.warehouseinventory.inventory.application.service;

import org.example.warehouseinventory.catalog.domain.dto.response.ReorderProjection;
import org.example.warehouseinventory.inventory.domain.entity.Lot;

import java.util.List;

public interface LotService {

    List<Lot> findExpiredLotsWithStock();
    List<ReorderProjection> findProductsBelowReorderPoint();

}