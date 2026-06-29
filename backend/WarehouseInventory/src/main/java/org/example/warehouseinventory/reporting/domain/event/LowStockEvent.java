package org.example.warehouseinventory.reporting.domain.event;

import java.time.LocalDate;
import java.util.UUID;

public record LowStockEvent(
        UUID product,
        String sku,
        String name,
        Integer currentStock,
        Integer minStockLevel
) { }