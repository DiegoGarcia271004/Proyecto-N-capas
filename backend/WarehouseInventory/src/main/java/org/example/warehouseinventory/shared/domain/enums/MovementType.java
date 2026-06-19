package org.example.warehouseinventory.shared.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MovementType implements JsonCreatable<MovementType> {

    ENTRY,
    EXIT,
    ADJUSTMENT,
    TRANSFER_OUT,
    TRANSFER_IN;

    @JsonCreator
    public static MovementType fromValue(String value) {
        return JsonCreatable.fromValue(value, MovementType.class);
    }
}