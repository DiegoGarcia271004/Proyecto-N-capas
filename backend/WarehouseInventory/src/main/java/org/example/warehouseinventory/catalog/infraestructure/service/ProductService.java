package org.example.warehouseinventory.catalog.infraestructure.service;

import lombok.RequiredArgsConstructor;
import org.example.warehouseinventory.catalog.domain.Product;
import org.example.warehouseinventory.catalog.domain.exception.ProductAlreadyExistsException;
import org.example.warehouseinventory.catalog.infraestructure.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(Product product) {
        if (productRepository.existsBySku(product.getSku())){
            throw new ProductAlreadyExistsException("A product with that SKU already exists");
        }

        productRepository.save(product);
    }
}
