package org.example.warehouseinventory.reporting.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.example.warehouseinventory.shared.domain.enums.JsonCreatable;

public enum AbcCategory implements JsonCreatable<AbcCategory> {
    A,
    B,
    C;

    @JsonCreator
    public static AbcCategory fromValue(String value) {
        return JsonCreatable.fromValue(value, AbcCategory.class);
    }
}
