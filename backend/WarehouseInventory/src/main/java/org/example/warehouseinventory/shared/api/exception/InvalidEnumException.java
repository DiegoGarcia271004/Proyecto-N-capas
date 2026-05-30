package org.example.warehouseinventory.shared.api.exception;

import org.springframework.http.HttpStatus;

public class InvalidEnumException extends ApiException {
    public InvalidEnumException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_ENUM_NAME");
    }
}
