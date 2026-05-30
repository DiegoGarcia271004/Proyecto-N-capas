package org.example.warehouseinventory.catalog.api.mapper;

import org.example.warehouseinventory.catalog.domain.dto.request.CreateProductRequest;
import org.example.warehouseinventory.catalog.domain.dto.request.UpdateProductRequest;
import org.example.warehouseinventory.catalog.domain.dto.response.ProductResponse;
import org.example.warehouseinventory.catalog.domain.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ProductMapper {
    public Product toEntityCreate(CreateProductRequest req) {
        return Product.builder()
                .sku(req.sku())
                .name(req.name())
                .dimensions(req.dimensions())
                .weight(req.weight())
                .minStockLevel(req.minStockLevel())
                .reorderPoint(req.reorderPoint())
                .productCategory(req.category())
                .storageRequirement(req.requirements())
                .build();
    }

    public Product toEntityUpdate(UUID id, UpdateProductRequest req) {
        return Product.builder()
                .id(id)
                .sku(req.sku())
                .name(req.name())
                .dimensions(req.dimensions())
                .weight(req.weight())
                .minStockLevel(req.minStockLevel())
                .reorderPoint(req.reorderPoint())
                .productCategory(req.category())
                .storageRequirement(req.requirements())
                .active(req.active())
                .build();
    }

    public Product toEntityResponse(ProductResponse res) {
        return Product.builder()
                .id(res.getId())
                .sku(res.getSku())
                .name(res.getName())
                .dimensions(res.getDimensions())
                .weight(res.getWeight())
                .minStockLevel(res.getMinStockLevel())
                .reorderPoint(res.getReorderPoint())
                .productCategory(res.getCategory())
                .storageRequirement(res.getStorageRequirement())
                .active(res.getActive())
                .build();
    }

    public ProductResponse toDto(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .dimensions(product.getDimensions())
                .weight(product.getWeight())
                .minStockLevel(product.getMinStockLevel())
                .reorderPoint(product.getReorderPoint())
                .category(product.getProductCategory())
                .storageRequirement(product.getStorageRequirement())
                .active(product.getActive())
                .build();
    }

    public List<ProductResponse> toDtoList(List<Product> products) {
        return products.stream().map(this::toDto).toList();
    }
}
