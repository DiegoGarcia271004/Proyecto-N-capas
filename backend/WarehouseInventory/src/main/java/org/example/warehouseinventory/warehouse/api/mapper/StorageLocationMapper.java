package org.example.warehouseinventory.warehouse.api.mapper;

import org.example.warehouseinventory.warehouse.domain.dto.response.StorageLocationResponse;
import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StorageLocationMapper {

    public StorageLocationResponse toDto(StorageLocation sl) {

        return new StorageLocationResponse(
                sl.getId(),
                sl.getWarehouse().getId(),
                sl.getWarehouse().getName(),
                sl.getCode(),
                sl.getZone(),
                sl.getMaxCapacity(),
                sl.getCurrentOccupancy(),
                sl.getAvailable(),
                sl.getCreatedAt(),
                sl.getCreatedBy()
        );
    }

    public List<StorageLocationResponse> toDtoList(List<StorageLocation> list) {
        return list.stream().map(this::toDto).toList();
    }
}