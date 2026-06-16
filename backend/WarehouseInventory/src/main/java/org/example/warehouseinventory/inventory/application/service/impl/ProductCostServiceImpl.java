package org.example.warehouseinventory.inventory.application.service.impl;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.catalog.api.mapper.ProductMapper;
import org.example.warehouseinventory.catalog.application.service.ProductService;
import org.example.warehouseinventory.catalog.domain.entity.Product;
import org.example.warehouseinventory.inventory.application.service.ProductCostService;
import org.example.warehouseinventory.inventory.domain.entity.ProductCost;
import org.example.warehouseinventory.inventory.infrastructure.repository.ProductCostRepository;
import org.example.warehouseinventory.warehouse.application.service.WarehouseService;
import org.example.warehouseinventory.warehouse.domain.entity.Warehouse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductCostServiceImpl implements ProductCostService {

    private final ProductCostRepository productCostRepository;
    private final ProductService productService;
    private final ProductMapper productMapper;
    private final WarehouseService warehouseService;

    @Override
    @Transactional
    public void recalculate(UUID product, UUID warehouse, BigDecimal newCost, Integer newQuantity) {

        Optional<ProductCost> existing = productCostRepository.findByProductIdAndWarehouseId(product, warehouse);

        if (existing.isPresent()) {

            existing.get().recalculate(newCost, newQuantity);
            productCostRepository.save(existing.get());

        } else {

            Product _product = productService.getProductEntityById(product);

            Warehouse _warehouse = warehouseService.getWarehouseById(warehouse);

            ProductCost productCost = ProductCost.builder()
                    .product(_product)
                    .warehouse(_warehouse)
                    .averageCost(newCost)
                    .totalQuantity(newQuantity)
                    .build();

            productCostRepository.save(productCost);
        }
    }
}