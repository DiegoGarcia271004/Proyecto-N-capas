package org.example.warehouseinventory.inventory.domain.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record LotResponse(

        UUID id,
        UUID product,
        String productSku,
        UUID warehouse,
        UUID storageLocation,
        String lotNumber,
        Integer quantity,
        Integer availableQuantity,
        LocalDate expirationDate,
        LocalDateTime receivedAt
) { }