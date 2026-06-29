package org.example.warehouseinventory.inventory.domain.exception;

import org.example.warehouseinventory.shared.api.exception.ApiException;
import org.springframework.http.HttpStatus;

public class InsufficientStockException extends ApiException {

    public InsufficientStockException(String productSku, int requested, int available) {

        super(
                "Insufficient stock for product " + productSku + ". Requested: " + requested + ". Available: " + available,
                HttpStatus.UNPROCESSABLE_ENTITY,
                "INSUFFICIENT_STOCK"
        );
    }
}