package org.example.warehouseinventory.reporting.api.mapper;

import org.example.warehouseinventory.reporting.domain.dto.response.CyclicCountResponse;
import org.example.warehouseinventory.reporting.domain.entity.CyclicCount;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CyclicCountMapper {

    public CyclicCountResponse toDto(CyclicCount cc) {

        return new CyclicCountResponse(
                cc.getId(),
                cc.getLot(),
                cc.getSystemQuantity(),
                cc.getPhysicalQuantity(),
                cc.getDiscrepancy(),
                cc.getStatus(),
                cc.getCreatedAt(),
                cc.getCreatedBy(),
                cc.getUpdatedAt(),
                cc.getUpdatedBy()
        );
    }

    public List<CyclicCountResponse> toDtoList(List<CyclicCount> list) {
        return list.stream().map(this::toDto).toList();
    }
}