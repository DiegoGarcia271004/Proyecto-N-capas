package org.example.warehouseinventory.reporting.domain.event;

import java.time.LocalDate;
import java.util.UUID;

public record ExpiredLotEvent(
        UUID lot,
        String lotNumber,
        String productSku,
        LocalDate expirationDate,
        Integer availableQuantity
) { }