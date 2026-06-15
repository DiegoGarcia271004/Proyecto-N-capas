package org.example.warehouseinventory.catalog.application.service;

import org.example.warehouseinventory.catalog.api.mapper.ProductMapper;
import org.example.warehouseinventory.catalog.application.service.impl.ProductServiceImpl;
import org.example.warehouseinventory.catalog.domain.dto.request.CreateProductRequest;
import org.example.warehouseinventory.catalog.domain.dto.request.UpdateProductRequest;
import org.example.warehouseinventory.catalog.domain.dto.response.ProductResponse;
import org.example.warehouseinventory.catalog.domain.entity.Product;
import org.example.warehouseinventory.catalog.infraestructure.repository.ProductRepository;
import org.example.warehouseinventory.shared.api.exception.BusinessRuleViolationException;
import org.example.warehouseinventory.shared.api.exception.ResourceNotFoundException;
import org.example.warehouseinventory.shared.domain.Dimensions;
import org.example.warehouseinventory.shared.domain.Weight;
import org.example.warehouseinventory.shared.domain.enums.DimensionUnit;
import org.example.warehouseinventory.shared.domain.enums.ProductCategory;
import org.example.warehouseinventory.shared.domain.enums.StorageRequirement;
import org.example.warehouseinventory.shared.domain.enums.WeightUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;
    @Mock
    ProductMapper productMapper;
    @InjectMocks
    ProductServiceImpl productService;

    // ── createProduct ──────────────────────────────────────────────

    @Test
    void createProduct_success_returnsResponse() {
        CreateProductRequest req = buildCreateRequest("SKU-001");
        Product product = buildProduct("SKU-001");
        ProductResponse expected = buildProductResponse("SKU-001");

        when(productRepository.existsBySku("SKU-001")).thenReturn(false);
        when(productMapper.toEntityCreate(req)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(expected);

        ProductResponse result = productService.createProduct(req);

        assertThat(result.sku()).isEqualTo("SKU-001");
        verify(productRepository).save(product);
    }

    @Test
    void createProduct_duplicateSku_throwsBusinessRuleViolation() {
        when(productRepository.existsBySku("SKU-001")).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(buildCreateRequest("SKU-001")))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("SKU-001");

        verify(productRepository, never()).save(any());
    }

    // ── getProductById ─────────────────────────────────────────────

    @Test
    void getProductById_exists_returnsResponse() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct("SKU-001");
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(buildProductResponse("SKU-001"));

        ProductResponse result = productService.getProductById(id);

        assertThat(result).isNotNull();
        assertThat(result.sku()).isEqualTo("SKU-001");
    }

    @Test
    void getProductById_notFound_throwsResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── getProductBySku ────────────────────────────────────────────

    @Test
    void getProductBySku_exists_returnsResponse() {
        Product product = buildProduct("SKU-001");
        when(productRepository.findBySku("SKU-001")).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(buildProductResponse("SKU-001"));

        assertThat(productService.getProductBySku("SKU-001").sku()).isEqualTo("SKU-001");
    }

    @Test
    void getProductBySku_notFound_throwsResourceNotFoundException() {
        when(productRepository.findBySku("SKU-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductBySku("SKU-999"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── getProductsByCategory ──────────────────────────────────────

    @Test
    void getProductsByCategory_returnsFilteredList() {
        List<Product> products = List.of(buildProduct("SKU-001"), buildProduct("SKU-002"));
        when(productRepository.findByProductCategory(ProductCategory.ELECTRONICS))
                .thenReturn(products);
        when(productMapper.toDtoList(products))
                .thenReturn(List.of(
                        buildProductResponse("SKU-001"),
                        buildProductResponse("SKU-002")
                ));

        List<ProductResponse> result = productService.getProductsByCategory(ProductCategory.ELECTRONICS);

        assertThat(result).hasSize(2);
        verify(productRepository).findByProductCategory(ProductCategory.ELECTRONICS);
    }

    @Test
    void getProductsByCategory_emptyResult_returnsEmptyList() {
        when(productRepository.findByProductCategory(any())).thenReturn(List.of());
        when(productMapper.toDtoList(List.of())).thenReturn(List.of());

        assertThat(productService.getProductsByCategory(ProductCategory.ELECTRONICS)).isEmpty();
    }

    // ── getAllProducts ─────────────────────────────────────────────

    @Test
    void getAllProducts_returnsAllInactiveProducts() {
        List<Product> products = List.of(buildProduct("SKU-001"));
        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toDtoList(products)).thenReturn(List.of(buildProductResponse("SKU-001")));

        assertThat(productService.getAllProducts()).hasSize(1);
        verify(productRepository).findAll();
    }

    // ── getProductIncludingInactive ────────────────────────────────

    @Test
    void getProductIncludingInactive_exists_returnsResponse() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct("SKU-001");
        when(productRepository.findByIdIncludingInactive(id)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(buildProductResponse("SKU-001"));

        assertThat(productService.getProductIncludingInactive(id)).isNotNull();
    }

    @Test
    void getProductIncludingInactive_notFound_throwsResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(productRepository.findByIdIncludingInactive(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductIncludingInactive(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── updateProduct ──────────────────────────────────────────────

    @Test
    void updateProduct_success_returnsUpdatedResponse() {
        UUID id = UUID.randomUUID();
        Product product = buildProductWithId(id, "SKU-001");
        UpdateProductRequest req = buildUpdateRequest(id, "SKU-001");

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.findBySku("SKU-001")).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(buildProductResponse("SKU-001"));

        ProductResponse result = productService.updateProduct(id, req);

        assertThat(result).isNotNull();
        verify(productMapper).updateEntity(product, req);
        verify(productRepository).save(product);
    }

    @Test
    void updateProduct_skuBelongsToAnotherProduct_throwsBusinessRuleViolation() {
        UUID id = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();
        Product product = buildProductWithId(id, "SKU-001");
        Product other = buildProductWithId(otherId, "SKU-TAKEN");

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.findBySku("SKU-TAKEN")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> productService.updateProduct(id, buildUpdateRequest(id, "SKU-TAKEN")))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("SKU-TAKEN");

        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_notFound_throwsResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(id, buildUpdateRequest(id, "SKU-001")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── deactivateProduct ──────────────────────────────────────────

    @Test
    void deactivateProduct_activeProduct_deactivatesAndSaves() {
        UUID id = UUID.randomUUID();
        Product product = buildProductWithId(id, "SKU-001");
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(buildProductResponse("SKU-001"));

        productService.deactivateProduct(id);

        assertThat(product.getActive()).isFalse();
        verify(productRepository).save(product);
    }

    @Test
    void deactivateProduct_notFound_throwsResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deactivateProduct(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── activateProduct ────────────────────────────────────────────

    @Test
    void activateProduct_inactiveProduct_activatesAndSaves() {
        UUID id = UUID.randomUUID();
        Product product = buildProductWithId(id, "SKU-001");
        product.deactivate();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(buildProductResponse("SKU-001"));

        productService.activateProduct(id);

        assertThat(product.getActive()).isTrue();
        verify(productRepository).save(product);
    }

    @Test
    void activateProduct_notFound_throwsResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.activateProduct(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── Fixtures ───────────────────────────────────────────────────

    private Product buildProduct(String sku) {
        return Product.builder()
                .id(UUID.randomUUID())
                .sku(sku)
                .name("Producto test")
                .productCategory(ProductCategory.ELECTRONICS)
                .storageRequirement(StorageRequirement.AMBIENT)
                .minStockLevel(10)
                .reorderPoint(20)
                .weight(Weight.of(new BigDecimal("1.250"), WeightUnit.KG))
                .dimensions(Dimensions.of(
                        new BigDecimal("10.5"),
                        new BigDecimal("20.0"),
                        new BigDecimal("5.0"),
                        DimensionUnit.CM))
                .active(true)
                .build();
    }

    private Product buildProductWithId(UUID id, String sku) {
        return Product.builder()
                .id(id)
                .sku(sku)
                .name("Producto test")
                .productCategory(ProductCategory.ELECTRONICS)
                .storageRequirement(StorageRequirement.AMBIENT)
                .minStockLevel(10)
                .reorderPoint(20)
                .weight(Weight.of(new BigDecimal("1.250"), WeightUnit.KG))
                .dimensions(Dimensions.of(
                        new BigDecimal("10.5"),
                        new BigDecimal("20.0"),
                        new BigDecimal("5.0"),
                        DimensionUnit.CM))
                .active(true)
                .build();
    }

    private CreateProductRequest buildCreateRequest(String sku) {
        return new CreateProductRequest(
                sku, "Producto test",
                ProductCategory.ELECTRONICS,
                10, 20,
                Weight.of(new BigDecimal("1.250"), WeightUnit.KG),
                Dimensions.of(
                        new BigDecimal("10.5"),
                        new BigDecimal("20.0"),
                        new BigDecimal("5.0"),
                        DimensionUnit.CM),
                StorageRequirement.AMBIENT
        );
    }

    private UpdateProductRequest buildUpdateRequest(UUID id, String sku) {
        return new UpdateProductRequest(
                id, sku, "Producto test",
                ProductCategory.ELECTRONICS,
                10, 20,
                Weight.of(new BigDecimal("1.250"), WeightUnit.KG),
                Dimensions.of(
                        new BigDecimal("10.5"),
                        new BigDecimal("20.0"),
                        new BigDecimal("5.0"),
                        DimensionUnit.CM),
                StorageRequirement.AMBIENT
        );
    }

    private ProductResponse buildProductResponse(String sku) {
        return new ProductResponse(
                UUID.randomUUID(), sku, "Producto test",
                Dimensions.of(
                        new BigDecimal("10.5"),
                        new BigDecimal("20.0"),
                        new BigDecimal("5.0"),
                        DimensionUnit.CM),
                Weight.of(new BigDecimal("1.250"), WeightUnit.KG),
                10, 20,
                ProductCategory.ELECTRONICS,
                StorageRequirement.AMBIENT,
                true
        );
    }
}