package org.example.warehouseinventory.catalog.api.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.catalog.application.service.impl.ProductServiceImpl;
import org.example.warehouseinventory.catalog.domain.dto.request.CreateProductRequest;
import org.example.warehouseinventory.catalog.domain.dto.request.UpdateProductRequest;
import org.example.warehouseinventory.shared.api.BaseController;
import org.example.warehouseinventory.shared.domain.GeneralResponse;
import org.example.warehouseinventory.shared.domain.enums.ProductCategory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController extends BaseController {

    private final ProductServiceImpl productService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> createProduct(@Valid @RequestBody CreateProductRequest req) {
        return buildResponse(
                "Product created successfully",
                HttpStatus.CREATED,
                productService.createProduct(req)
        );
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'OPERATOR')")
    public ResponseEntity<GeneralResponse> getProductById(@PathVariable UUID id) {
        return buildResponse(
                "Product found",
                HttpStatus.OK,
                productService.getProductById(id)
        );
    }

    @GetMapping("/sku/{sku}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'OPERATOR')")
    public ResponseEntity<GeneralResponse> getProductBySku(@PathVariable String sku) {
        return buildResponse(
                "Product found",
                HttpStatus.OK,
                productService.getProductBySku(sku)
        );
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'OPERATOR')")
    public ResponseEntity<GeneralResponse> getProductsByCategory(@PathVariable ProductCategory category) {
        return buildResponse(
                "Products found",
                HttpStatus.OK,
                productService.getProductsByCategory(category)
        );
    }

    @GetMapping("/inactive")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> getAllProducts() {
        return buildResponse(
                "Products found",
                HttpStatus.OK,
                productService.getAllProducts()
        );
    }

    @GetMapping("/id/inactive/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> getInactiveProductById(@PathVariable UUID id) {
        return buildResponse(
                "Product found",
                HttpStatus.OK,
                productService.getProductIncludingInactive(id)
        );
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> updateProduct(@PathVariable UUID id, @Valid @RequestBody UpdateProductRequest req) {
        return buildResponse(
                "Product updated successfully",
                HttpStatus.OK,
                productService.updateProduct(id, req)
        );
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> deactivateProduct(@PathVariable UUID id) {
        return buildResponse(
                "Product deactivated successfully",
                HttpStatus.OK,
                productService.deactivateProduct(id)
        );
    }

    @PutMapping("/activate/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public ResponseEntity<GeneralResponse> activateProduct(@PathVariable UUID id) {
        return buildResponse(
                "Product activated successfully",
                HttpStatus.OK,
                productService.activateProduct(id)
        );
    }
}

