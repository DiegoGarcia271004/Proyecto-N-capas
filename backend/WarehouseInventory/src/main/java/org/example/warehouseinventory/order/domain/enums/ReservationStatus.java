package org.example.warehouseinventory.order.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.example.warehouseinventory.shared.domain.enums.JsonCreatable;

public enum ReservationStatus implements JsonCreatable<ReservationStatus> {
    ACTIVE,
    RELEASED,
    CONFIRMED,
    EXPIRED;

    @JsonCreator
    public static ReservationStatus fromValue(String value) {
        return JsonCreatable.fromValue(value, ReservationStatus.class);
    }
}
