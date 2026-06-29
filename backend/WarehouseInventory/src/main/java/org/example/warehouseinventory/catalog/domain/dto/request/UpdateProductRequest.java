package org.example.warehouseinventory.catalog.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.warehouseinventory.shared.domain.Dimensions;
import org.example.warehouseinventory.shared.domain.Weight;
import org.example.warehouseinventory.shared.domain.enums.ProductCategory;
import org.example.warehouseinventory.shared.domain.enums.StorageRequirement;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequest(
        @NotNull
        UUID id,

        @NotBlank(message = "SKU is required")
        String sku,

        @NotBlank(message = "A name is required")
        String name,

        @NotNull(message = "A category is required")
        ProductCategory category,

        @NotNull
        @Positive
        Integer minStockLevel,

        @NotNull
        @Positive
        Integer reorderPoint,

        @NotNull
        Weight weight,

        @NotNull
        Dimensions dimensions,

        @NotNull
        StorageRequirement requirements,

        @NotNull(message = "Lead time days is required.")
        @Positive(message = "Lead time days must be greater than zero.")
        Integer leadTimeDays,

        @NotNull(message = "Safety stock is required.")
        @Positive(message = "Safety stock must be greater than zero.")
        Integer safetyStock
) {
}
