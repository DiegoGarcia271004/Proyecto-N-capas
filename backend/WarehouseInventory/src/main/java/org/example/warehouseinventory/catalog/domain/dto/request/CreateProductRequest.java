package org.example.warehouseinventory.catalog.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.warehouseinventory.shared.domain.Dimensions;
import org.example.warehouseinventory.shared.domain.Weight;
import org.example.warehouseinventory.shared.domain.enums.ProductCategory;
import org.example.warehouseinventory.shared.domain.enums.StorageRequirement;

import java.math.BigDecimal;

public record CreateProductRequest(

    @NotNull
    @NotBlank(message = "SKU is required")
    String sku,

    @NotNull
    @NotBlank(message = "A name is required")
    String name,

    @NotNull(message = "A category is required")
    @NotBlank
    ProductCategory category,

    @NotNull
    @NotBlank
    @Positive
    BigDecimal minStockLevel,

    @NotNull
    @NotBlank
    @Positive
    BigDecimal reorderPoint,

    @NotNull
    @NotBlank
    Weight weight,

    @NotNull
    @NotBlank
    Dimensions dimensions,

    @NotNull
    @NotBlank
    StorageRequirement requirements
){}
