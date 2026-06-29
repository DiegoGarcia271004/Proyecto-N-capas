package org.example.warehouseinventory.shared.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AssignmentStrategy implements JsonCreatable<AssignmentStrategy> {
    NEAREST_EXIT,
    AISLE_FILL,
    ZONE_BASED,
    SEASONAL_PROXIMITY,
    RANDOM;

    @JsonCreator
    public static AssignmentStrategy fromValue(String value) {
        return JsonCreatable.fromValue(value, AssignmentStrategy.class);
    }
}