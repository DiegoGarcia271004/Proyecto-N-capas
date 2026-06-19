package org.example.warehouseinventory.order.application.service;

import org.example.warehouseinventory.catalog.application.service.ProductService;
import org.example.warehouseinventory.catalog.domain.entity.Product;
import org.example.warehouseinventory.inventory.application.service.StockReservationService;
import org.example.warehouseinventory.inventory.domain.dto.LotReservationDetail;
import org.example.warehouseinventory.inventory.domain.entity.Lot;
import org.example.warehouseinventory.inventory.domain.exception.InsufficientStockException;
import org.example.warehouseinventory.order.api.mapper.ReservationMapper;
import org.example.warehouseinventory.order.application.service.impl.ReservationServiceImpl;
import org.example.warehouseinventory.order.domain.dto.request.ReservationRequest;
import org.example.warehouseinventory.order.domain.dto.response.ReservationResponse;
import org.example.warehouseinventory.order.domain.entity.Reservation;
import org.example.warehouseinventory.order.domain.entity.ReservationLot;
import org.example.warehouseinventory.order.domain.enums.ReservationStatus;
import org.example.warehouseinventory.order.infrastructure.repository.ReservationLotRepository;
import org.example.warehouseinventory.order.infrastructure.repository.ReservationRepository;
import org.example.warehouseinventory.shared.api.exception.BusinessRuleViolationException;
import org.example.warehouseinventory.shared.api.exception.ResourceNotFoundException;
import org.example.warehouseinventory.shared.domain.Dimensions;
import org.example.warehouseinventory.shared.domain.Weight;
import org.example.warehouseinventory.shared.domain.enums.DimensionUnit;
import org.example.warehouseinventory.shared.domain.enums.ProductCategory;
import org.example.warehouseinventory.shared.domain.enums.StorageRequirement;
import org.example.warehouseinventory.shared.domain.enums.WeightUnit;
import org.example.warehouseinventory.warehouse.application.service.WarehouseService;
import org.example.warehouseinventory.warehouse.domain.entity.StorageLocation;
import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class ReservationServiceTest {

    @Mock
    ReservationRepository reservationRepository;
    @Mock
    ReservationLotRepository reservationLotRepository;
    @Mock
    WarehouseService warehouseService;
    @Mock
    ProductService productService;
    @Mock
    StockReservationService stockReservationService;
    @Mock
    ReservationMapper mapper;

    ReservationServiceImpl reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationServiceImpl(
                reservationRepository,
                reservationLotRepository,
                warehouseService,
                productService,
                stockReservationService,
                mapper
        );
    }

    // ── createReservation ────────────────────────────────────────────

    @Test
    void createReservation_success_reservesStockAndSavesReservationLots() {
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        UUID lotId = UUID.randomUUID();

        Product product = buildProduct(productId);
        Warehouse warehouse = buildWarehouse(warehouseId);
        Lot lot = buildLot(lotId, product, warehouse);

        ReservationRequest req = new ReservationRequest(productId, warehouseId, 30, 15);
        List<LotReservationDetail> details = List.of(new LotReservationDetail(lotId, 30));
        ReservationResponse expectedResponse = buildReservationResponse(productId, warehouseId);

        when(productService.getProductEntityById(productId)).thenReturn(product);
        when(warehouseService.getWarehouseById(warehouseId)).thenReturn(warehouse);
        when(stockReservationService.reserveStock(productId, warehouseId, 30)).thenReturn(details);
        when(stockReservationService.getLotEntityById(lotId)).thenReturn(lot);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));
        when(mapper.toDto(any(Reservation.class))).thenReturn(expectedResponse);

        ReservationResponse result = reservationService.createReservation(req);

        assertThat(result).isEqualTo(expectedResponse);
        verify(stockReservationService).reserveStock(productId, warehouseId, 30);
        verify(reservationRepository).save(any(Reservation.class));
        verify(reservationLotRepository).save(any(ReservationLot.class));
    }

    @Test
    void createReservation_productNotFound_throwsResourceNotFoundException() {
        UUID productId = UUID.randomUUID();
        ReservationRequest req = new ReservationRequest(productId, UUID.randomUUID(), 30, 15);

        when(productService.getProductEntityById(productId))
                .thenThrow(new ResourceNotFoundException("A product with this id does not exist"));

        assertThatThrownBy(() -> reservationService.createReservation(req))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(stockReservationService, never()).reserveStock(any(), any(), any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createReservation_warehouseNotFound_throwsResourceNotFoundException() {
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        Product product = buildProduct(productId);

        ReservationRequest req = new ReservationRequest(productId, warehouseId, 30, 15);

        when(productService.getProductEntityById(productId)).thenReturn(product);
        when(warehouseService.getWarehouseById(warehouseId))
                .thenThrow(new ResourceNotFoundException("A warehouse with this id does not exist"));

        assertThatThrownBy(() -> reservationService.createReservation(req))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createReservation_insufficientStock_throwsInsufficientStockException() {
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        Product product = buildProduct(productId);
        Warehouse warehouse = buildWarehouse(warehouseId);

        ReservationRequest req = new ReservationRequest(productId, warehouseId, 100, 15);

        when(productService.getProductEntityById(productId)).thenReturn(product);
        when(warehouseService.getWarehouseById(warehouseId)).thenReturn(warehouse);
        when(stockReservationService.reserveStock(productId, warehouseId, 100))
                .thenThrow(new InsufficientStockException(productId.toString(), 100, 30));

        assertThatThrownBy(() -> reservationService.createReservation(req))
                .isInstanceOf(InsufficientStockException.class);

        verify(reservationRepository, never()).save(any());
    }

    // ── confirmReservation ───────────────────────────────────────────

    @Test
    void confirmReservation_activeReservation_confirmsStockAndUpdatesStatus() {
        UUID id = UUID.randomUUID();
        Reservation reservation = buildReservation(id, ReservationStatus.ACTIVE);
        List<ReservationLot> reservationLots = List.of();
        List<LotReservationDetail> details = List.of();

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));
        when(reservationLotRepository.findByReservationId(id)).thenReturn(reservationLots);
        when(mapper.toDetails(reservationLots)).thenReturn(details);
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        when(mapper.toDto(reservation)).thenReturn(buildReservationResponse(
                reservation.getProduct().getId(), reservation.getWarehouse().getId()));

        reservationService.confirmReservation(id);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        verify(stockReservationService).confirmStock(details);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void confirmReservation_notFound_throwsResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(reservationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.confirmReservation(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void confirmReservation_notActive_throwsBusinessRuleViolation() {
        UUID id = UUID.randomUUID();
        Reservation reservation = buildReservation(id, ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.confirmReservation(id))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("ACTIVE");

        verify(stockReservationService, never()).confirmStock(any());
        verify(reservationRepository, never()).save(any());
    }

    // ── releaseReservation ────────────────────────────────────────────

    @Test
    void releaseReservation_activeReservation_releasesStockAndUpdatesStatus() {
        UUID id = UUID.randomUUID();
        Reservation reservation = buildReservation(id, ReservationStatus.ACTIVE);
        List<ReservationLot> reservationLots = List.of();
        List<LotReservationDetail> details = List.of();

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));
        when(reservationLotRepository.findByReservationId(id)).thenReturn(reservationLots);
        when(mapper.toDetails(reservationLots)).thenReturn(details);
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        when(mapper.toDto(reservation)).thenReturn(buildReservationResponse(
                reservation.getProduct().getId(), reservation.getWarehouse().getId()));

        reservationService.releaseReservation(id);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RELEASED);
        verify(stockReservationService).releaseStock(details);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void releaseReservation_notFound_throwsResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(reservationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.releaseReservation(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void releaseReservation_notActive_throwsBusinessRuleViolation() {
        UUID id = UUID.randomUUID();
        Reservation reservation = buildReservation(id, ReservationStatus.RELEASED);

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.releaseReservation(id))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("ACTIVE");

        verify(stockReservationService, never()).releaseStock(any());
    }

    // ── releaseExpiredReservations ────────────────────────────────────

    @Test
    void releaseExpiredReservations_releasesAllExpired() {
        Reservation expired1 = buildReservation(UUID.randomUUID(), ReservationStatus.ACTIVE);
        Reservation expired2 = buildReservation(UUID.randomUUID(), ReservationStatus.ACTIVE);

        when(reservationRepository.findExpiredReservations())
                .thenReturn(List.of(expired1, expired2));
        when(reservationLotRepository.findByReservationId(any())).thenReturn(List.of());
        when(mapper.toDetails(any())).thenReturn(List.of());

        reservationService.releaseExpiredReservations();

        assertThat(expired1.getStatus()).isEqualTo(ReservationStatus.RELEASED);
        assertThat(expired2.getStatus()).isEqualTo(ReservationStatus.RELEASED);
        verify(stockReservationService, times(2)).releaseStock(any());
        verify(reservationRepository, times(2)).save(any(Reservation.class));
    }

    @Test
    void releaseExpiredReservations_noExpired_doesNothing() {
        when(reservationRepository.findExpiredReservations()).thenReturn(List.of());

        reservationService.releaseExpiredReservations();

        verify(stockReservationService, never()).releaseStock(any());
        verify(reservationRepository, never()).save(any());
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

    private Lot buildLot(UUID id, Product product, Warehouse warehouse) {
        StorageLocation location = StorageLocation.create(warehouse, "A-01", "ZONE-A", 100, 0);
        Lot lot = Lot.create(product, warehouse, location, "LOT-001", 50, LocalDate.now().plusMonths(6));
        ReflectionTestUtils.setField(lot, "id", id);
        return lot;
    }

    private Reservation buildReservation(UUID id, ReservationStatus status) {
        Product product = buildProduct(UUID.randomUUID());
        Warehouse warehouse = buildWarehouse(UUID.randomUUID());
        Reservation reservation = Reservation.create(product, warehouse, 30, LocalDateTime.now().plusMinutes(15));
        ReflectionTestUtils.setField(reservation, "id", id);
        ReflectionTestUtils.setField(reservation, "status", status);
        return reservation;
    }

    private ReservationResponse buildReservationResponse(UUID productId, UUID warehouseId) {
        return new ReservationResponse(
                UUID.randomUUID(), productId, warehouseId, "SKU-001",
                30, ReservationStatus.ACTIVE, LocalDateTime.now().plusMinutes(15)
        );
    }
}