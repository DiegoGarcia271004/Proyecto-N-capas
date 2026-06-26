package org.example.warehouseinventory.shared.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CountStatus implements JsonCreatable<CountStatus> {
    PENDING,
    IN_PROGRESS,
    ADJUSTED;

    @JsonCreator
    public static CountStatus fromValue(String value) {
        return JsonCreatable.fromValue(value, CountStatus.class);
    }
}