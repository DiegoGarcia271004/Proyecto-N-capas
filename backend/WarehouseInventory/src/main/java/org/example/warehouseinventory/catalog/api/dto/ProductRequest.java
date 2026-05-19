package org.example.warehouseinventory.catalog.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.example.warehouseinventory.shared.domain.enums.ProductCategory;

import java.math.BigDecimal;

public record ProductRequest(

    @NotBlank(message = "SKU is required")
    String sku,

    @NotBlank(message = "A name is required")
    String name,

    @NotNull(message = "A category is required")
    ProductCategory category,

    @Positive
    BigDecimal minStockLevel


){}
