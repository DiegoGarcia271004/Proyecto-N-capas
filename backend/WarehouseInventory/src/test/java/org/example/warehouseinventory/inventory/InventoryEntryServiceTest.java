package org.example.warehouseinventory.inventory;

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
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryEntryServiceTest {

    @Mock
    ProductService productService;
    @Mock
    WarehouseService warehouseService;
    @Mock
    StorageLocationService storageLocationService;
    @Mock
    LotRepository lotRepository;
    @Mock
    StockMovementRepository stockMovementRepository;
    @Mock
    InventoryMapper inventoryMapper;
    @Mock
    ProductMapper productMapper;

    InventoryEntryServiceImpl inventoryEntryService;

    @BeforeEach
    void setUp() {
        inventoryEntryService = new InventoryEntryServiceImpl(
                productService,
                productMapper,
                warehouseService,
                storageLocationService,
                lotRepository,
                stockMovementRepository,
                inventoryMapper
        );
    }

    @Test
    void registerEntry_success_createsLotAndMovement() {
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();

        Product product = buildProduct(productId);
        Warehouse warehouse = buildWarehouse(warehouseId);
        StorageLocation location = buildStorageLocation(warehouse);

        InventoryEntryRequest request = new InventoryEntryRequest(
                productId, warehouseId, "LOT-001", 50, LocalDate.now().plusMonths(6)
        );

        LotResponse expectedResponse = buildLotResponse(productId, warehouseId, locationId);

        when(productService.getProductEntityById(productId)).thenReturn(product);
        when(warehouseService.getWarehouseById(warehouseId)).thenReturn(warehouse);
        when(storageLocationService.findAvailableStorageLocation(warehouseId, 50))
                .thenReturn(location);
        when(lotRepository.save(any(Lot.class))).thenAnswer(i -> i.getArgument(0));
        when(inventoryMapper.toDto(any(Lot.class))).thenReturn(expectedResponse);

        try (MockedStatic<SecurityContextHolder> mockedSecurity = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContext(mockedSecurity, "admin");

            LotResponse result = inventoryEntryService.registerEntry(request);

            assertThat(result).isEqualTo(expectedResponse);
        }

        verify(lotRepository).save(any(Lot.class));
        verify(storageLocationService).updateOccupancy(location, 50);
        verify(stockMovementRepository).save(argThat(movement ->
                movement.getType() == MovementType.ENTRY &&
                        movement.getQuantity().equals(50)
        ));
    }

    @Test
    void registerEntry_productNotFound_throwsResourceNotFoundException() {
        UUID productId = UUID.randomUUID();
        InventoryEntryRequest request = new InventoryEntryRequest(
                productId, UUID.randomUUID(), "LOT-001", 50, LocalDate.now().plusMonths(6)
        );

        when(productService.getProductEntityById(productId))
                .thenThrow(new ResourceNotFoundException("A product with this id does not exist"));

        assertThatThrownBy(() -> inventoryEntryService.registerEntry(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(lotRepository, never()).save(any());
        verify(stockMovementRepository, never()).save(any());
    }

    @Test
    void registerEntry_warehouseNotFound_throwsResourceNotFoundException() {
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        Product product = buildProduct(productId);

        InventoryEntryRequest request = new InventoryEntryRequest(
                productId, warehouseId, "LOT-001", 50, LocalDate.now().plusMonths(6)
        );

        when(productService.getProductEntityById(productId)).thenReturn(product);
        when(warehouseService.getWarehouseById(warehouseId))
                .thenThrow(new ResourceNotFoundException("A warehouse with this id does not exist"));

        assertThatThrownBy(() -> inventoryEntryService.registerEntry(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(lotRepository, never()).save(any());
    }

    @Test
    void registerEntry_noAvailableStorageLocation_throwsBusinessRuleViolation() {
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        Product product = buildProduct(productId);
        Warehouse warehouse = buildWarehouse(warehouseId);

        InventoryEntryRequest request = new InventoryEntryRequest(
                productId, warehouseId, "LOT-001", 50, LocalDate.now().plusMonths(6)
        );

        when(productService.getProductEntityById(productId)).thenReturn(product);
        when(warehouseService.getWarehouseById(warehouseId)).thenReturn(warehouse);
        when(storageLocationService.findAvailableStorageLocation(warehouseId, 50))
                .thenThrow(new BusinessRuleViolationException("No available storage location"));

        assertThatThrownBy(() -> inventoryEntryService.registerEntry(request))
                .isInstanceOf(BusinessRuleViolationException.class);

        verify(lotRepository, never()).save(any());
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

    private StorageLocation buildStorageLocation(Warehouse warehouse) {
        return StorageLocation.create(warehouse, "A-01", "ZONE-A", 100, 0);
    }

    private LotResponse buildLotResponse(UUID productId, UUID warehouseId, UUID locationId) {
        return new LotResponse(
                UUID.randomUUID(), productId, "SKU-001",
                warehouseId, locationId,
                "LOT-001", 50, 50,
                LocalDate.now().plusMonths(6), LocalDateTime.now()
        );
    }

    private void mockSecurityContext(MockedStatic<SecurityContextHolder> mocked, String username) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn(username);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }
}