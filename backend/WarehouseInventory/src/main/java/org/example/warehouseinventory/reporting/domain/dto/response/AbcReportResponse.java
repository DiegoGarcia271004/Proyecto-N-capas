package org.example.warehouseinventory.reporting.domain.dto.response;

import org.example.warehouseinventory.reporting.domain.enums.AbcCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record AbcReportResponse(
        LocalDate from,
        LocalDate to,
        BigDecimal totalConsumptionValue,
        Map<AbcCategory, Long> countByCategory,
        List<AbcItemResponse> items
) {
}
