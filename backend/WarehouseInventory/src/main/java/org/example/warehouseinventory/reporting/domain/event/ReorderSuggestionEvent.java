package org.example.warehouseinventory.reporting.domain.event;

import java.util.UUID;

public record ReorderSuggestionEvent(

        UUID product,
        UUID warehouse,
        String productSku,
        Integer currentStock,
        Integer reorderPoint,
        Integer suggestedQuantity,
        Double avgDailyConsumption
) { }