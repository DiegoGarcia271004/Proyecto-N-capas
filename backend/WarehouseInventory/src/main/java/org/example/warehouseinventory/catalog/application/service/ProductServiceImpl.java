package org.example.warehouseinventory.catalog.application.service;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.catalog.domain.dto.request.CreateProductRequest;
import org.example.warehouseinventory.catalog.domain.dto.request.UpdateProductRequest;
import org.example.warehouseinventory.catalog.domain.dto.response.ProductResponse;
import org.example.warehouseinventory.catalog.api.mapper.ProductMapper;
import org.example.warehouseinventory.catalog.domain.entity.Product;
import org.example.warehouseinventory.catalog.infraestructure.repository.ProductRepository;
import org.example.warehouseinventory.shared.api.exception.ApiException;
import org.example.warehouseinventory.shared.api.exception.BusinessRuleViolationException;
import org.example.warehouseinventory.shared.domain.enums.ProductCategory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(CreateProductRequest req) {
        if (productRepository.existsBySku(req.sku())) {
            throw new BusinessRuleViolationException("SKU " + req.sku() + " already exists for another product");
        }

        Product p = productMapper.toEntityCreate(req);

        return productMapper.toDto(productRepository.save(p));
    }

    @Override
    public ProductResponse getProductById(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ApiException.ResourceNotFoundException("A product with this id does not exists");
        }

        return productMapper.toDto(productRepository.getReferenceById(id));
    }

    @Override
    public ProductResponse getProductBySku(String sku) {
        return productMapper.toDto(productRepository.findBySku(sku).orElseThrow(
                () -> new ApiException.ResourceNotFoundException("A product with this SKU does not exists")
        ));
    }

    @Override
    public List<ProductResponse> getProductsByCategory(ProductCategory category) {
        List<Product> products = productRepository.findAll();
        return productMapper.toDtoList(products.stream().filter(p -> p.getProductCategory().equals(category)).toList());
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productMapper.toDtoList(productRepository.findAll());
    }

    @Override
    public ProductResponse getProductIncludingInactive(UUID id) {
        return productMapper.toDto(
                productRepository.findByIdIncludingInactive(id).orElseThrow(() -> new ApiException.ResourceNotFoundException("A product with this ID does not exists"))
        );
    }

    @Override
    public ProductResponse updateProduct(UUID id, UpdateProductRequest req) {
        Product product = productMapper.toEntityResponse(this.getProductById(id));
        productRepository.findBySku(req.sku()).ifPresent( existing -> {
            if (!existing.getId().equals(id)) {
                throw new BusinessRuleViolationException("SKU " + req.sku() + " already exists for another product");
            }
        });

        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public ProductResponse deactivateProduct(UUID id) {
        Product product = productMapper.toEntityResponse(this.getProductById(id));
        product.deactivate();
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public ProductResponse activateProduct(UUID id) {
        Product product = productMapper.toEntityResponse(this.getProductById(id));
        product.activate();
        return productMapper.toDto(productRepository.save(product));
    }

}
