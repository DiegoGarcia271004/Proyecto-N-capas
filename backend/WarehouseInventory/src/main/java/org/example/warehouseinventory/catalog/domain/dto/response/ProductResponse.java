package org.example.warehouseinventory.catalog.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.warehouseinventory.shared.domain.Dimensions;
import org.example.warehouseinventory.shared.domain.Weight;
import org.example.warehouseinventory.shared.domain.enums.ProductCategory;
import org.example.warehouseinventory.shared.domain.enums.StorageRequirement;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ProductResponse(
        UUID id,
        String sku,
        String name,
        Dimensions dimensions,
        Weight weight,
        Integer minStockLevel,
        Integer reorderPoint,
        ProductCategory category,
        StorageRequirement storageRequirement,
        Boolean active
) {
}
