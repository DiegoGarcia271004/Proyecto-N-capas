package org.example.warehouseinventory.inventory.application.service;

import org.example.warehouseinventory.inventory.domain.dto.request.StockConsumptionRequest;

public interface StockConsumptionService {

    void consumeStock(StockConsumptionRequest request);
}