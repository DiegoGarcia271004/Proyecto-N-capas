package org.example.warehouseinventory.reporting.domain.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ReorderSuggestionResponse(
        UUID id,
        UUID productId,
        UUID warehouseId,
        String productSku,
        Integer currentStock,
        Integer reorderPoint,
        Integer suggestedQuantity,
        Double avgDailyConsumption,
        Boolean attended,
        Instant createdAt
) { }