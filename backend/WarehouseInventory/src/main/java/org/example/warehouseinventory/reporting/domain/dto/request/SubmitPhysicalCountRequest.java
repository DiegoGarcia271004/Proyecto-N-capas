package org.example.warehouseinventory.reporting.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SubmitPhysicalCountRequest(
        @NotNull(message = "Physical quantity is required")
        @PositiveOrZero(message = "Physical quantity must be zero or greater")
        Integer physicalQuantity
) {}