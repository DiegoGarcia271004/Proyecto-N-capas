package org.example.warehouseinventory.shared.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum WeightUnit implements JsonCreatable<WeightUnit> {
    KG, LBS, OZ, G;

    @JsonCreator
    public static ProductCategory fromValue(String value) {
        return JsonCreatable.fromValue(value, ProductCategory.class);
    }
}
