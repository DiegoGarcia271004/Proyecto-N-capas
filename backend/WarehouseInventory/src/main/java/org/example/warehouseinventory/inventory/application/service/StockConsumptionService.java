package org.example.warehouseinventory.inventory.application.service;

import java.util.UUID;

public interface StockConsumptionService {

    void consumeStock(UUID product, UUID warehouse, Integer quantityRequested);
}