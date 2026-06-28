package org.example.warehouseinventory.warehouse.api.mapper;

import org.example.warehouseinventory.warehouse.domain.dto.response.WarehouseResponse;
import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WarehouseMapper {

    public WarehouseResponse toDto(Warehouse warehouse) {

        return new WarehouseResponse(
                warehouse.getId(),
                warehouse.getName(),
                warehouse.getAddress(),
                warehouse.getActive(),
                warehouse.getCreatedAt(),
                warehouse.getCreatedBy(),
                warehouse.getUpdatedAt(),
                warehouse.getUpdatedBy()
        );
    }

    public List<WarehouseResponse> toDtoList(List<Warehouse> warehouses) {
        return warehouses.stream().map(this::toDto).toList();
    }
}