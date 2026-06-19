package org.example.warehouseinventory.inventory;

import org.example.warehouseinventory.inventory.application.service.impl.StockConsumptionServiceImpl;
import org.example.warehouseinventory.inventory.domain.dto.request.StockConsumptionRequest;
import org.example.warehouseinventory.inventory.domain.entity.StockMovement;
import org.example.warehouseinventory.inventory.domain.exception.InsufficientStockException;
import org.example.warehouseinventory.shared.domain.enums.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.example.warehouseinventory.catalog.application.service.ProductService;
import org.example.warehouseinventory.catalog.domain.entity.Product;
import org.example.warehouseinventory.inventory.domain.entity.Lot;
import org.example.warehouseinventory.inventory.infrastructure.repository.LotRepository;
import org.example.warehouseinventory.inventory.infrastructure.repository.StockMovementRepository;
import org.example.warehouseinventory.warehouse.application.service.StorageLocationService;
import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;
import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.example.warehouseinventory.shared.api.exception.ResourceNotFoundException;
import org.example.warehouseinventory.shared.domain.Dimensions;
import org.example.warehouseinventory.shared.domain.Weight;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockConsumptionServiceTest {

    @Mock ProductService productService;
    @Mock LotRepository lotRepository;
    @Mock StockMovementRepository stockMovementRepository;
    @Mock StorageLocationService storageLocationService;

    StockConsumptionServiceImpl stockConsumptionService;

    @BeforeEach
    void setUp() {
        stockConsumptionService = new StockConsumptionServiceImpl(
                productService,
                lotRepository,
                stockMovementRepository,
                storageLocationService
        );
    }

    @Test
    void consumeStock_singleLotCoversRequest_consumesOnlyThatLot() {
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();

        Product product = buildProduct(productId);
        Warehouse warehouse = buildWarehouse(warehouseId);
        StorageLocation location = StorageLocation.create(warehouse, "A-01", "ZONE-A", 100, 50);

        Lot lot = Lot.create(product, warehouse, location, "LOT-001", 100, LocalDate.now().plusMonths(6));
        // availableQuantity inicial = 100 (set en create)

        StockConsumptionRequest request = new StockConsumptionRequest(productId, warehouseId, 30);

        when(productService.getProductEntityById(productId)).thenReturn(product);
        when(lotRepository.findAvailableLotsFifo(productId, warehouseId)).thenReturn(List.of(lot));

        stockConsumptionService.consumeStock(request);

        assertThat(lot.getAvailableQuantity()).isEqualTo(70);
        verify(lotRepository).save(lot);
        verify(storageLocationService).releaseOccupancy(location, 30);
        verify(stockMovementRepository).save(argThat(movement ->
                movement.getType() == MovementType.EXIT &&
                        movement.getQuantity().equals(30)
        ));
    }

    @Test
    void consumeStock_multipleLotsFifo_consumesInOrderUntilSatisfied() {
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();

        Product product = buildProduct(productId);
        Warehouse warehouse = buildWarehouse(warehouseId);
        StorageLocation location = StorageLocation.create(warehouse, "A-01", "ZONE-A", 100, 50);

        Lot lot1 = Lot.create(product, warehouse, location, "LOT-001", 20, LocalDate.now().plusMonths(1));
        Lot lot2 = Lot.create(product, warehouse, location, "LOT-002", 50, LocalDate.now().plusMonths(6));

        StockConsumptionRequest request = new StockConsumptionRequest(productId, warehouseId, 30);

        when(productService.getProductEntityById(productId)).thenReturn(product);
        when(lotRepository.findAvailableLotsFifo(productId, warehouseId))
                .thenReturn(List.of(lot1, lot2)); // orden FIFO ya viene del repo

        stockConsumptionService.consumeStock(request);

        // lot1 se agota completamente (20), lot2 cubre el resto (10)
        assertThat(lot1.getAvailableQuantity()).isEqualTo(0);
        assertThat(lot2.getAvailableQuantity()).isEqualTo(40);

        verify(lotRepository, times(2)).save(any(Lot.class));
        verify(stockMovementRepository, times(2)).save(any(StockMovement.class));
    }

    @Test
    void consumeStock_insufficientStock_throwsInsufficientStockException() {
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();

        Product product = buildProduct(productId);
        Warehouse warehouse = buildWarehouse(warehouseId);
        StorageLocation location = StorageLocation.create(warehouse, "A-01", "ZONE-A", 100, 50);

        Lot lot = Lot.create(product, warehouse, location, "LOT-001", 10, LocalDate.now().plusMonths(1));

        StockConsumptionRequest request = new StockConsumptionRequest(productId, warehouseId, 50);

        when(productService.getProductEntityById(productId)).thenReturn(product);
        when(lotRepository.findAvailableLotsFifo(productId, warehouseId)).thenReturn(List.of(lot));

        assertThatThrownBy(() -> stockConsumptionService.consumeStock(request))
                .isInstanceOf(InsufficientStockException.class);

        verify(lotRepository, never()).save(any());
        verify(stockMovementRepository, never()).save(any());
    }

    @Test
    void consumeStock_noLotsAvailable_throwsInsufficientStockException() {
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        Product product = buildProduct(productId);

        StockConsumptionRequest request = new StockConsumptionRequest(productId, warehouseId, 10);

        when(productService.getProductEntityById(productId)).thenReturn(product);
        when(lotRepository.findAvailableLotsFifo(productId, warehouseId)).thenReturn(List.of());

        assertThatThrownBy(() -> stockConsumptionService.consumeStock(request))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void consumeStock_productNotFound_throwsResourceNotFoundException() {
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();

        when(productService.getProductEntityById(productId))
                .thenThrow(new ResourceNotFoundException("A product with this id does not exist"));

        assertThatThrownBy(() -> stockConsumptionService.consumeStock(
                new StockConsumptionRequest(productId, warehouseId, 10)))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(lotRepository, never()).findAvailableLotsFifo(any(), any());
    }

    // ── Fixtures ───────────────────────────────────────────────────

    private Product buildProduct(UUID id) {
        Product product = Product.create(
                "SKU-001", "Producto test",
                Dimensions.of(new BigDecimal("10.5"), new BigDecimal("20.0"), new BigDecimal("5.0"), DimensionUnit.CM),
                Weight.of(new BigDecimal("1.250"), WeightUnit.KG),
                10, 20,
                ProductCategory.ELECTRONICS, StorageRequirement.AMBIENT
        );
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }

    private Warehouse buildWarehouse(UUID id) {
        Warehouse warehouse = Warehouse.create("Almacén Central", "Zona Industrial");
        ReflectionTestUtils.setField(warehouse, "id", id);
        return warehouse;
    }
}