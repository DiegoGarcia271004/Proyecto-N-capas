package org.example.warehouseinventory.inventory.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record StockConsumptionRequest(

        @NotNull(message = "IDs product is required.")
        UUID product,

        @NotNull(message = "IDs warehouse is required.")
        UUID warehouse,

        @NotNull(message = "Quantity is required.")
        @Positive(message = "Quantity must be greater than zero.")
        Integer quantity
) { }