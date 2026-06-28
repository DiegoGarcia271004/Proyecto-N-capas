package org.example.warehouseinventory.warehouse.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateStorageLocationRequest(

        @NotNull(message = "Warehouse id is required")
        UUID warehouse,

        @NotBlank(message = "Code is required")
        String code,

        @NotBlank(message = "Zone is required")
        String zone,

        @NotNull(message = "Max capacity is required")
        @Positive(message = "Max capacity must be greater than zero")
        Integer maxCapacity
) {}