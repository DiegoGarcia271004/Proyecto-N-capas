package org.example.warehouseinventory.reporting.api.mapper;

import org.example.warehouseinventory.reporting.domain.dto.response.ReorderSuggestionResponse;
import org.example.warehouseinventory.reporting.domain.entity.ReorderSuggestion;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReorderSuggestionMapper {

    public ReorderSuggestionResponse toDto(ReorderSuggestion rs) {
        return new ReorderSuggestionResponse(
                rs.getId(),
                rs.getProduct(),
                rs.getWarehouse(),
                rs.getProductSku(),
                rs.getCurrentStock(),
                rs.getReorderPoint(),
                rs.getSuggestedQuantity(),
                rs.getAvgDailyConsumption(),
                rs.getAttended(),
                rs.getCreatedAt()
        );
    }

    public List<ReorderSuggestionResponse> toDtoList(List<ReorderSuggestion> list) {
        return list.stream().map(this::toDto).toList();
    }
}