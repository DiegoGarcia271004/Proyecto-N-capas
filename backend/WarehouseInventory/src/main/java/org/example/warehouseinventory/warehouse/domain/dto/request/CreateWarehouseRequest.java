package org.example.warehouseinventory.warehouse.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateWarehouseRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Address is required")
        String address
) {}