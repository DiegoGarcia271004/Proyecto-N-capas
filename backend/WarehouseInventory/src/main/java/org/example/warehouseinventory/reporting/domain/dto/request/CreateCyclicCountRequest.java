package org.example.warehouseinventory.reporting.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateCyclicCountRequest(
        @NotNull(message = "Lot's id is required")
        UUID lot
) {}