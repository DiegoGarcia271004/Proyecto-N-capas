package org.example.warehouseinventory.inventory.infrastructure.repository.projection;

import java.util.UUID;

public interface ExitSummaryProjection {
    UUID getProductId();
    UUID getWarehouseId();
    Integer getTotalExitQuantity();
}
