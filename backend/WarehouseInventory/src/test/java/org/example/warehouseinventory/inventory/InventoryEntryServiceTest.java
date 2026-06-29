package org.example.warehouseinventory.inventory;

import org.example.warehouseinventory.inventory.application.service.ProductCostService;
import org.example.warehouseinventory.inventory.application.service.StockMovementService;
import org.example.warehouseinventory.inventory.domain.dto.response.ProductWarehouseExitSummary;
import org.example.warehouseinventory.reporting.application.service.impl.AbcReportServiceImpl;
import org.example.warehouseinventory.reporting.domain.dto.response.AbcReportResponse;
import org.example.warehouseinventory.reporting.domain.enums.AbcCategory;
import org.example.warehouseinventory.shared.domain.enums.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.example.warehouseinventory.catalog.api.mapper.ProductMapper;
import org.example.warehouseinventory.catalog.application.service.ProductService;
import org.example.warehouseinventory.catalog.domain.entity.Product;
import org.example.warehouseinventory.inventory.api.mapper.InventoryMapper;
import org.example.warehouseinventory.inventory.application.service.impl.InventoryEntryServiceImpl;
import org.example.warehouseinventory.inventory.domain.dto.request.InventoryEntryRequest;
import org.example.warehouseinventory.inventory.domain.dto.response.LotResponse;
import org.example.warehouseinventory.inventory.domain.entity.Lot;
import org.example.warehouseinventory.inventory.infrastructure.repository.LotRepository;
import org.example.warehouseinventory.inventory.infrastructure.repository.StockMovementRepository;
import org.example.warehouseinventory.warehouse.application.service.StorageLocationService;
import org.example.warehouseinventory.warehouse.application.service.WarehouseService;
import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;
import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.example.warehouseinventory.shared.api.exception.BusinessRuleViolationException;
import org.example.warehouseinventory.shared.api.exception.ResourceNotFoundException;
import org.example.warehouseinventory.shared.domain.Dimensions;
import org.example.warehouseinventory.shared.domain.Weight;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryEntryServiceTest {

    @Mock
    private ProductService productService;
    @Mock
    private StockMovementService stockMovementService;
    @Mock
    private ProductCostService productCostService;

    private AbcReportServiceImpl abcReportService;

    private static final LocalDate FROM = LocalDate.of(2024, 1, 1);
    private static final LocalDate TO = LocalDate.of(2024, 12, 31);

    @BeforeEach
    void setUp() {
        abcReportService = new AbcReportServiceImpl(productService, stockMovementService, productCostService);
    }
    // --- happy path ---

    @Test
    void generateAbcReport_returnsEmptyReport_whenNoExitMovements() {
        when(stockMovementService.getExitSummaryByProductAndWarehouse(FROM, TO)).thenReturn(List.of());

        AbcReportResponse result = abcReportService.generateAbcReport(FROM, TO);

        assertThat(result.items()).isEmpty();
        assertThat(result.totalConsumptionValue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.countByCategory()).isEmpty();
    }

    @Test
    void generateAbcReport_classifiesItemsCorrectly_acrossFourItems() {
        // Item 1: 100 × $10 = $1000 → 50%  acumulado       → A
        // Item 2:  60 × $10 = $600  → 30%  acumulado 80%   → A  (exactamente en umbral)
        // Item 3:  30 × $10 = $300  → 15%  acumulado 95%   → B  (exactamente en umbral)
        // Item 4:  10 × $10 = $100  →  5%  acumulado 100%  → C
        UUID warehouseId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        UUID productId3 = UUID.randomUUID();
        UUID productId4 = UUID.randomUUID();

        Product product1 = mockProduct(productId1, "SKU-1", "Product 1");
        Product product2 = mockProduct(productId2, "SKU-2", "Product 2");
        Product product3 = mockProduct(productId3, "SKU-3", "Product 3");
        Product product4 = mockProduct(productId4, "SKU-4", "Product 4");

        when(stockMovementService.getExitSummaryByProductAndWarehouse(FROM, TO)).thenReturn(List.of(
                new ProductWarehouseExitSummary(productId1, warehouseId, 100),
                new ProductWarehouseExitSummary(productId2, warehouseId, 60),
                new ProductWarehouseExitSummary(productId3, warehouseId, 30),
                new ProductWarehouseExitSummary(productId4, warehouseId, 10)
        ));

        when(productService.getProductEntityById(productId1)).thenReturn(product1);
        when(productService.getProductEntityById(productId2)).thenReturn(product2);
        when(productService.getProductEntityById(productId3)).thenReturn(product3);
        when(productService.getProductEntityById(productId4)).thenReturn(product4);
        when(productCostService.getAverageCost(any(UUID.class), eq(warehouseId)))
                .thenReturn(Optional.of(new BigDecimal("10.00")));

        AbcReportResponse result = abcReportService.generateAbcReport(FROM, TO);

        assertThat(result.totalConsumptionValue()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(result.items()).hasSize(4);

        assertThat(result.items().get(0).category()).isEqualTo(AbcCategory.A);
        assertThat(result.items().get(1).category()).isEqualTo(AbcCategory.A);
        assertThat(result.items().get(2).category()).isEqualTo(AbcCategory.B);
        assertThat(result.items().get(3).category()).isEqualTo(AbcCategory.C);

        assertThat(result.countByCategory()).containsEntry(AbcCategory.A, 2L);
        assertThat(result.countByCategory()).containsEntry(AbcCategory.B, 1L);
        assertThat(result.countByCategory()).containsEntry(AbcCategory.C, 1L);
    }

    @Test
    void generateAbcReport_itemsSortedByConsumptionValueDescending_regardlessOfInputOrder() {
        UUID warehouseId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID(); // mayor valor
        UUID productId2 = UUID.randomUUID(); // menor valor

        Product product1 = mockProduct(productId1, "SKU-1", "Product 1");
        Product product2 = mockProduct(productId2, "SKU-2", "Product 2");

        // llegan en orden invertido
        when(stockMovementService.getExitSummaryByProductAndWarehouse(FROM, TO)).thenReturn(List.of(
                new ProductWarehouseExitSummary(productId2, warehouseId, 10),
                new ProductWarehouseExitSummary(productId1, warehouseId, 100)
        ));

        when(productService.getProductEntityById(productId1)).thenReturn(product1);
        when(productService.getProductEntityById(productId2)).thenReturn(product2);
        when(productCostService.getAverageCost(any(UUID.class), eq(warehouseId)))
                .thenReturn(Optional.of(new BigDecimal("10.00")));

        AbcReportResponse result = abcReportService.generateAbcReport(FROM, TO);

        assertThat(result.items().get(0).productId()).isEqualTo(productId1);
        assertThat(result.items().get(1).productId()).isEqualTo(productId2);
    }

    // --- manejo de costo ausente ---

    @Test
    void generateAbcReport_usesZeroCost_whenProductCostNotFound() {
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();

        Product product = mockProduct(productId, "SKU-1", "Product 1");

        when(stockMovementService.getExitSummaryByProductAndWarehouse(FROM, TO))
                .thenReturn(List.of(new ProductWarehouseExitSummary(productId, warehouseId, 50)));
        when(productService.getProductEntityById(productId)).thenReturn(product);
        when(productCostService.getAverageCost(productId, warehouseId)).thenReturn(Optional.empty());

        AbcReportResponse result = abcReportService.generateAbcReport(FROM, TO);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).consumptionValue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.items().get(0).averageCost()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.items().get(0).category()).isEqualTo(AbcCategory.C);
    }

    // --- helper ---

    private Product mockProduct(UUID id, String sku, String name) {
        return Product.create(
                sku,
                name,
                Dimensions.of(new BigDecimal("10"), new BigDecimal("10"), new BigDecimal("10"), DimensionUnit.CM),
                Weight.of(new BigDecimal("1.0"), WeightUnit.KG),
                10,
                20,
                ProductCategory.OTHER,
                StorageRequirement.AMBIENT,
                5,
                15
        );
    }

}