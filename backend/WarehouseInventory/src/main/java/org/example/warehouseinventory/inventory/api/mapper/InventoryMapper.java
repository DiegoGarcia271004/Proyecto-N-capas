package org.example.warehouseinventory.inventory.api.mapper;

import org.example.warehouseinventory.inventory.domain.dto.response.LotResponse;
import org.example.warehouseinventory.inventory.domain.entity.Lot;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public LotResponse toDto(Lot lot) {

        return LotResponse.builder()
                .id(lot.getId())
                .product(lot.getProduct().getId())
                .productSku(lot.getProduct().getSku())
                .warehouse(lot.getWarehouse().getId())
                .storageLocation(lot.getStorageLocation().getId())
                .lotNumber(lot.getLotNumber())
                .quantity(lot.getQuantity())
                .availableQuantity(lot.getAvailableQuantity())
                .expirationDate(lot.getExpirationDate())
                .receivedAt(lot.getReceivedAt())
                .build();
    }
}