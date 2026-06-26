package org.example.warehouseinventory.reporting.domain.dto.response;

import org.example.warehouseinventory.shared.domain.enums.CountStatus;

import java.time.Instant;
import java.util.UUID;

public record CyclicCountResponse(
        UUID id,
        UUID lot,
        Integer systemQuantity,
        Integer physicalQuantity,
        Integer discrepancy,
        CountStatus status,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy
) {}