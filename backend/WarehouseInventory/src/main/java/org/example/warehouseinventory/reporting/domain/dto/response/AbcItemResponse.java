package org.example.warehouseinventory.reporting.domain.dto.response;

import org.example.warehouseinventory.reporting.domain.enums.AbcCategory;

import java.math.BigDecimal;
import java.util.UUID;

public record AbcItemResponse(
        UUID productId,
        String productSku,
        String productName,
        UUID warehouseId,
        Integer totalExitQuantity,
        BigDecimal averageCost,
        BigDecimal consumptionValue,
        BigDecimal cumulativePercentage,
        AbcCategory category
) {
}
