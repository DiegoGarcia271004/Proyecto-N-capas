package org.example.warehouseinventory.inventory.domain.dto.response;

import java.util.UUID;

public record ProductWarehouseExitSummary(
        UUID productId,
        UUID warehouseId,
        Integer totalExitQuantity
) {}