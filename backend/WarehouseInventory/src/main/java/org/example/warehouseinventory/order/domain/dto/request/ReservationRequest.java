package org.example.warehouseinventory.order.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ReservationRequest(
        @NotNull(message = "Product is required")
        UUID productId,

        @NotNull(message = "Warehouse is required")
        UUID warehouseId,

        @NotNull
        @Positive
        Integer quantity,

        @NotNull
        @Positive
        Integer expirationMinutes
) {
}
