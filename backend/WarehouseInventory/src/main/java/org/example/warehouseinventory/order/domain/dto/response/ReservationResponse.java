package org.example.warehouseinventory.order.domain.dto.response;

import org.example.warehouseinventory.order.domain.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        UUID productId,
        UUID warehouseId,
        String productSku,
        Integer quantity,
        ReservationStatus status,
        LocalDateTime expiresAt
) {
}
