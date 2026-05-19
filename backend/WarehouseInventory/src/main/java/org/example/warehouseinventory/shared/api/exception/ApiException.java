package org.example.warehouseinventory.shared.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApiException extends RuntimeException{
    private final HttpStatus status;
    private final String code;

    protected ApiException(String message, HttpStatus status, String code){
        super(message);
        this.status = status;
        this.code = code;
    }

}
