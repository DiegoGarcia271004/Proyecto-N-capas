package org.example.warehouseinventory.shared.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DimensionUnit implements JsonCreatable<DimensionUnit> {
    KG, LBS, OZ, G;

    @JsonCreator
    public static DimensionUnit fromValue(String value) {
        return JsonCreatable.fromValue(value, DimensionUnit.class);
    }
}
