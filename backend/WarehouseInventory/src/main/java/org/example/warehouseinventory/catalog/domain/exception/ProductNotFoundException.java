package org.example.warehouseinventory.catalog.domain.exception;

import org.example.warehouseinventory.shared.api.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends ApiException {
    public ProductNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND");
    }
}
