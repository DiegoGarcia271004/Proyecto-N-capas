package org.example.warehouseinventory.reporting.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.catalog.application.service.ProductService;
import org.example.warehouseinventory.inventory.application.service.ProductCostService;
import org.example.warehouseinventory.inventory.application.service.StockMovementService;
import org.example.warehouseinventory.inventory.domain.dto.response.ProductWarehouseExitSummary;
import org.example.warehouseinventory.reporting.application.service.AbcReportService;
import org.example.warehouseinventory.reporting.domain.dto.response.AbcItemResponse;
import org.example.warehouseinventory.reporting.domain.dto.response.AbcReportResponse;
import org.example.warehouseinventory.reporting.domain.enums.AbcCategory;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AbcReportServiceImpl implements AbcReportService {

    private static final BigDecimal THRESHOLD_A = new BigDecimal("80.00");
    private static final BigDecimal THRESHOLD_B = new BigDecimal("95.00");

    private final ProductService productService;
    private final StockMovementService stockMovementService;
    private final ProductCostService productCostService;

    @Override
    @Transactional(readOnly = true)
    public AbcReportResponse generateAbcReport(LocalDate from, LocalDate to) {
        List<ProductWarehouseExitSummary>  exitSummaries =
                stockMovementService.getExitSummaryByProductAndWarehouse(from, to);

        if (exitSummaries.isEmpty()) {
            return new AbcReportResponse(from, to, BigDecimal.ZERO, Map.of(), List.of());
        }

        List<AbcItemResponse> unsortedItems = exitSummaries.stream()
                .map(summary -> {
                    var product = productService.getProductEntityById(summary.productId());
                    BigDecimal avgCost = productCostService
                            .getAverageCost(summary.productId(), summary.warehouseId())
                            .orElse(BigDecimal.ZERO);
                    BigDecimal consumptionValue = avgCost
                            .multiply(BigDecimal.valueOf(summary.totalExitQuantity()));

                    return new AbcItemResponse(
                            product.getId(),
                            product.getSku(),
                            product.getName(),
                            summary.warehouseId(),
                            summary.totalExitQuantity(),
                            avgCost,
                            consumptionValue,
                            BigDecimal.ZERO,
                            null
                    );
                })
                .sorted(Comparator.comparing(AbcItemResponse::consumptionValue).reversed())
                .toList();

        BigDecimal totalValue = unsortedItems.stream()
                .map(AbcItemResponse::consumptionValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalValue.compareTo(BigDecimal.ZERO) == 0) {
            List<AbcItemResponse> allC = unsortedItems.stream()
                    .map(item -> new AbcItemResponse(
                            item.productId(), item.productSku(), item.productName(),
                            item.warehouseId(), item.totalExitQuantity(),
                            item.averageCost(), item.consumptionValue(),
                            BigDecimal.ZERO, AbcCategory.C
                    ))
                    .toList();
            return new AbcReportResponse(from, to, BigDecimal.ZERO,
                    Map.of(AbcCategory.C, (long) allC.size()), allC);
        }

        List<AbcItemResponse> classifiedItems = getAbcItemResponses(unsortedItems, totalValue);

        Map<AbcCategory, Long> countByCategory = classifiedItems.stream()
                .collect(Collectors.groupingBy(AbcItemResponse::category, Collectors.counting()));

        return new AbcReportResponse(from, to, totalValue, countByCategory, classifiedItems);
    }

    private static @NonNull List<AbcItemResponse> getAbcItemResponses(List<AbcItemResponse> unsortedItems, BigDecimal totalValue) {
        List<AbcItemResponse> classifiedItems = new ArrayList<>();
        BigDecimal cumulative = BigDecimal.ZERO;

        for (AbcItemResponse item : unsortedItems) {
            BigDecimal itemPercentage = totalValue.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : item.consumptionValue()
                      .multiply(new BigDecimal("100"))
                      .divide(totalValue, 2, RoundingMode.HALF_UP);

            cumulative = cumulative.add(itemPercentage);

            AbcCategory category;
            if (cumulative.compareTo(THRESHOLD_A) <= 0) {
                category = AbcCategory.A;
            } else if (cumulative.compareTo(THRESHOLD_B) <= 0) {
                category = AbcCategory.B;
            } else {
                category = AbcCategory.C;
            }

            classifiedItems.add(new AbcItemResponse(
                    item.productId(),
                    item.productSku(),
                    item.productName(),
                    item.warehouseId(),
                    item.totalExitQuantity(),
                    item.averageCost(),
                    item.consumptionValue(),
                    cumulative,
                    category
            ));
        }

        return classifiedItems;
    }
}
