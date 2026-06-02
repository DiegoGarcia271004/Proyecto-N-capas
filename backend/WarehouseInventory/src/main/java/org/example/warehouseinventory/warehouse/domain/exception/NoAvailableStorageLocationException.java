package org.example.warehouseinventory.warehouse.domain.exception;

import org.example.warehouseinventory.shared.api.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class NoAvailableStorageLocationException extends ApiException {

    public NoAvailableStorageLocationException(UUID warehouse, int requested, int available) {
        super(
                "No available storage location in warehouse: " + warehouse + ". Requested: " + requested + ". Available: " + available,
                HttpStatus.UNPROCESSABLE_CONTENT,
                "NO_AVAILABLE_STORAGE_LOCATION"
        );
    }
}