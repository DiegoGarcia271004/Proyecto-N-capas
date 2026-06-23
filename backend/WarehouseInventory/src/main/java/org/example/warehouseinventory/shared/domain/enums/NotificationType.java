package org.example.warehouseinventory.shared.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum NotificationType implements JsonCreatable<NotificationType> {

    LOW_STOCK,
    EXPIRED_LOT;

    @JsonCreator
    public static NotificationType fromValue(String value) {
        return JsonCreatable.fromValue(value, NotificationType.class);
    }
}