package org.example.warehouseinventory.inventory.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InventoryEntryRequest(

        @NotNull(message = "IDs product is required.")
        UUID product,

        @NotNull(message = "IDs warehouse is required.")
        UUID warehouse,

        @NotNull(message = "Lot number is required.")
        String lotNumber,

        @NotNull(message = "Quantity is required.")
        @Positive(message = "Quantity must be greater than zero.")
        Integer quantity,

        @NotNull(message = "Unit cost is required.")
        @Positive(message = "Unit cost must be greater than zero.")
        BigDecimal unitCost,

        LocalDate expirationDate
) { }