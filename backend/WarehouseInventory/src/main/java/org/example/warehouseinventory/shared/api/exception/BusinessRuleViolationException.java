package org.example.warehouseinventory.shared.api.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleViolationException extends ApiException {
    public BusinessRuleViolationException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_CONTENT, "BUSINESS_RULE_VIOLATION");
    }
}
