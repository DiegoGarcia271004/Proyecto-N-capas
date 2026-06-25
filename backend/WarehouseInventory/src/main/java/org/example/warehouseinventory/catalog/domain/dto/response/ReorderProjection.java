package org.example.warehouseinventory.catalog.domain.dto.response;

import java.util.UUID;

public interface ReorderProjection {

        UUID getProductId();
        String getSku();
        UUID getWarehouseId();
        Integer getCurrentStock();
        Integer getReorderPoint();
        Integer getLeadTimeDays();
        Integer getSafetyStock();
}