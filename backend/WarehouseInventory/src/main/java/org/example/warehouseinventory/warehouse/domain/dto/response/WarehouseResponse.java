package org.example.warehouseinventory.warehouse.domain.dto.response;

import java.time.Instant;
import java.util.UUID;

public record WarehouseResponse(
        UUID id,
        String name,
        String address,
        Boolean active,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy
) {}