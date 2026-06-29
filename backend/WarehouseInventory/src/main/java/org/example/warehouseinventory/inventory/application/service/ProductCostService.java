package org.example.warehouseinventory.inventory.application.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ProductCostService {

    void recalculate(UUID product, UUID warehouse, BigDecimal newCost, Integer newQuantity);
    Optional<BigDecimal> getAverageCost(UUID productId, UUID warehouseId);
}