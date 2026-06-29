package org.example.warehouseinventory.catalog.application.service;

import org.example.warehouseinventory.catalog.domain.dto.request.CreateProductRequest;
import org.example.warehouseinventory.catalog.domain.dto.request.UpdateProductRequest;
import org.example.warehouseinventory.catalog.domain.dto.response.LowStockProjection;
import org.example.warehouseinventory.catalog.domain.dto.response.ProductResponse;
import org.example.warehouseinventory.catalog.domain.entity.Product;
import org.example.warehouseinventory.shared.domain.enums.ProductCategory;

import java.util.List;
import java.util.UUID;


public interface ProductService {
    ProductResponse createProduct(CreateProductRequest req);
    ProductResponse getProductById(UUID id);
    ProductResponse getProductBySku(String sku);
    Product getProductEntityById(UUID id);
    List<ProductResponse> getProductsByCategory(ProductCategory category);
    ProductResponse getProductIncludingInactive(UUID id);
    ProductResponse updateProduct(UUID id, UpdateProductRequest req);
    ProductResponse deactivateProduct(UUID id);
    ProductResponse activateProduct(UUID id);
    List<ProductResponse> getAllProducts();
    List<LowStockProjection> findProductsBelowMinStock();
}
