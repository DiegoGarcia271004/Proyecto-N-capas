package org.example.warehouseinventory.inventory.domain.dto;

import java.util.UUID;

public record LotReservationDetail(
        UUID lotId,
        Integer quantity
) {
}
