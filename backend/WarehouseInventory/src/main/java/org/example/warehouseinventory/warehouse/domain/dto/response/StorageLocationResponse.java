package org.example.warehouseinventory.warehouse.domain.dto.response;

import java.time.Instant;
import java.util.UUID;

public record StorageLocationResponse(
        UUID id,
        UUID warehouseId,
        String warehouseName,
        String code,
        String zone,
        Integer maxCapacity,
        Integer currentOccupancy,
        Boolean available,
        Instant createdAt,
        String createdBy
) {}