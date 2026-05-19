package org.example.warehouseinventory.catalog.domain.exception;

import org.example.warehouseinventory.shared.api.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ProductAlreadyExistsException extends ApiException {
    public ProductAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT, "PRODUCT_ALREADY_EXISTS");
    }
}
