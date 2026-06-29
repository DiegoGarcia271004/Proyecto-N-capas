package org.example.warehouseinventory.catalog.domain.dto.response;

import java.util.UUID;

public interface LowStockProjection {

    UUID getProductId();
    String getSku();
    String getName();
    Integer getCurrentStock();
    Integer getMinStockLevel();
}