package org.example.warehouseinventory.catalog.api;

import org.example.warehouseinventory.auth.application.service.UserDetailsServiceImpl;
import org.example.warehouseinventory.catalog.api.controller.ProductController;
import org.example.warehouseinventory.catalog.application.service.impl.ProductServiceImpl;
import org.example.warehouseinventory.catalog.domain.dto.request.CreateProductRequest;
import org.example.warehouseinventory.catalog.domain.dto.request.UpdateProductRequest;
import org.example.warehouseinventory.catalog.domain.dto.response.ProductResponse;
import org.example.warehouseinventory.shared.api.exception.BusinessRuleViolationException;
import org.example.warehouseinventory.shared.api.exception.ResourceNotFoundException;
import org.example.warehouseinventory.shared.domain.Dimensions;
import org.example.warehouseinventory.shared.domain.Weight;
import org.example.warehouseinventory.shared.domain.enums.DimensionUnit;
import org.example.warehouseinventory.shared.domain.enums.ProductCategory;
import org.example.warehouseinventory.shared.domain.enums.StorageRequirement;
import org.example.warehouseinventory.shared.domain.enums.WeightUnit;
import org.example.warehouseinventory.shared.utils.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.http.MediaType.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
public class ProductControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserDetailsServiceImpl userDetailsService;
    @MockitoBean
    ProductServiceImpl productService;

    @Test
    void createProduct_noToken_return401() throws Exception {
        mockMvc.perform(post("/api/product")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCreateRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void createProduct_operatorRole_returns403() throws Exception {
        mockMvc.perform(post("/api/product")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCreateRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_validRequest_returns201() throws Exception {
        when(productService.createProduct(any())).thenReturn(buildProductResponse());

        mockMvc.perform(post("/api/product")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCreateRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Product created successfully"))
                .andExpect(jsonPath("$.data.sku").value("SKU-001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_invalidBody_returns400() throws Exception {
        mockMvc.perform(post("/api/product")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_duplicateSku_returns422() throws Exception {
        when(productService.createProduct(any()))
                .thenThrow(new BusinessRuleViolationException("SKU-001"));

        mockMvc.perform(post("/api/product")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCreateRequest())))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"));
    }

    @Test
    void getProductById_noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/product/id/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void getProductById_exists_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.getProductById(id)).thenReturn(buildProductResponse());

        mockMvc.perform(get("/api/product/id/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sku").value("SKU-001"));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void getProductById_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.getProductById(id))
                .thenThrow(new ResourceNotFoundException(id.toString()));

        mockMvc.perform(get("/api/product/id/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void getProductBySku_exists_returns200() throws Exception {
        when(productService.getProductBySku("SKU-001")).thenReturn(buildProductResponse());

        mockMvc.perform(get("/api/product/sku/{sku}", "SKU-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sku").value("SKU-001"));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void getProductBySku_notFound_returns404() throws Exception {
        when(productService.getProductBySku("SKU-999"))
                .thenThrow(new ResourceNotFoundException("SKU-999"));

        mockMvc.perform(get("/api/product/sku/{sku}", "SKU-999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void getProductsByCategory_validCategory_returns200() throws Exception {
        when(productService.getProductsByCategory(ProductCategory.ELECTRONICS))
                .thenReturn(List.of(buildProductResponse()));

        mockMvc.perform(get("/api/product/category/{category}", "ELECTRONICS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].sku").value("SKU-001"));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void getProductsByCategory_invalidCategory_returns400() throws Exception {
        mockMvc.perform(get("/api/product/category/{category}", "CATEGORIA_INVALIDA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ENUM_VALUE"));
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void getProductsByCategory_emptyResult_returns200WithEmptyList() throws Exception {
        when(productService.getProductsByCategory(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/product/category/{category}", "ELECTRONICS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void getAllProducts_noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/product/inactive"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void getAllProducts_operatorRole_returns403() throws Exception {
        mockMvc.perform(get("/api/product/inactive"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_MANAGER")
    void getAllProducts_returns200() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(buildProductResponse()));

        mockMvc.perform(get("/api/product/inactive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    // ── GET /api/product/id/inactive/{id} ─────────────────────────

    @Test
    @WithMockUser(roles = "OPERATOR")
    void getInactiveProductById_operatorRole_returns403() throws Exception {
        mockMvc.perform(get("/api/product/id/inactive/{id}", UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_MANAGER")
    void getInactiveProductById_exists_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.getProductIncludingInactive(id)).thenReturn(buildProductResponse());

        mockMvc.perform(get("/api/product/id/inactive/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sku").value("SKU-001"));
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_MANAGER")
    void getInactiveProductById_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.getProductIncludingInactive(id))
                .thenThrow(new ResourceNotFoundException(id.toString()));

        mockMvc.perform(get("/api/product/id/inactive/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OPERATOR")
    void updateProduct_operatorRole_returns403() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(put("/api/product/update/{id}", id)
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUpdateRequest(id))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_MANAGER")
    void updateProduct_validRequest_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.updateProduct(eq(id), any())).thenReturn(buildProductResponse());

        mockMvc.perform(put("/api/product/update/{id}", id)
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUpdateRequest(id))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product updated successfully"));
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_MANAGER")
    void updateProduct_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.updateProduct(eq(id), any()))
                .thenThrow(new ResourceNotFoundException(id.toString()));

        mockMvc.perform(put("/api/product/update/{id}", id)
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUpdateRequest(id))))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_MANAGER")
    void updateProduct_skuConflict_returns422() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.updateProduct(eq(id), any()))
                .thenThrow(new BusinessRuleViolationException("SKU-TAKEN"));

        mockMvc.perform(put("/api/product/update/{id}", id)
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUpdateRequest(id))))
                .andExpect(status().isUnprocessableContent());
    }


    @Test
    @WithMockUser(roles = "OPERATOR")
    void deactivateProduct_operatorRole_returns403() throws Exception {
        mockMvc.perform(delete("/api/product/delete/{id}", UUID.randomUUID()).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_MANAGER")
    void deactivateProduct_activeProduct_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.deactivateProduct(id)).thenReturn(buildProductResponse());

        mockMvc.perform(delete("/api/product/delete/{id}", id).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product deactivated successfully"));
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_MANAGER")
    void deactivateProduct_alreadyInactive_returns422() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.deactivateProduct(id))
                .thenThrow(new BusinessRuleViolationException("Product is already inactive"));

        mockMvc.perform(delete("/api/product/delete/{id}", id).with(csrf()))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"));
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_MANAGER")
    void activateProduct_inactiveProduct_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.activateProduct(id)).thenReturn(buildProductResponse());

        mockMvc.perform(put("/api/product/activate/{id}", id).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product activated successfully"));
    }

    @Test
    @WithMockUser(roles = "WAREHOUSE_MANAGER")
    void activateProduct_alreadyActive_returns422() throws Exception {
        UUID id = UUID.randomUUID();
        when(productService.activateProduct(id))
                .thenThrow(new BusinessRuleViolationException("Product is already active"));

        mockMvc.perform(put("/api/product/activate/{id}", id).with(csrf()))
                .andExpect(status().isUnprocessableContent());
    }

    private ProductResponse buildProductResponse() {
        return new ProductResponse(
                UUID.randomUUID(),
                "SKU-001",
                "Producto test",
                Dimensions.of(new BigDecimal("10.0"),
                        new BigDecimal("20.0"),
                        new BigDecimal("5.0"),
                        DimensionUnit.CM),
                Weight.of(
                        new BigDecimal("1.250"),
                        WeightUnit.KG
                ),
                10,
                20,
                ProductCategory.ELECTRONICS,
                StorageRequirement.AMBIENT,
                true,
                5,
                15
        );
    }

    private UpdateProductRequest buildUpdateRequest(UUID id) {
        return new UpdateProductRequest(
                id,
                "SKU-001",
                "Producto test",
                ProductCategory.ELECTRONICS,
                10,
                20,
                Weight.of(new BigDecimal("1.250"), WeightUnit.KG),
                Dimensions.of(
                        new BigDecimal("10.5"),
                        new BigDecimal("20.0"),
                        new BigDecimal("5.0"),
                        DimensionUnit.CM
                ),
                StorageRequirement.AMBIENT,
                5,
                15
        );
    }

    private CreateProductRequest buildCreateRequest() {
        return new CreateProductRequest(
                "SKU-001",
                "Producto test",
                ProductCategory.ELECTRONICS,
                10,
                20,
                Weight.of(new BigDecimal("1.250"), WeightUnit.KG),
                Dimensions.of(
                        new BigDecimal("10.5"),
                        new BigDecimal("20.0"),
                        new BigDecimal("5.0"),
                        DimensionUnit.CM
                ),
                StorageRequirement.AMBIENT,
                5,
                15
        );
    }
}
