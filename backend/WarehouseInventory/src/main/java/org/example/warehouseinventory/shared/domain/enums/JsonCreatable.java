package org.example.warehouseinventory.shared.domain.enums;

import org.example.warehouseinventory.shared.api.exception.InvalidEnumException;

import java.util.Arrays;

public interface JsonCreatable<T extends Enum<T>> {
    static <T extends Enum<T>> T fromValue(String value, Class<T> enumClass) {
        for (T constant : enumClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(value)) {
                return constant;
            }
        }

        throw new InvalidEnumException("Invalid value '" + value + "' for " + enumClass.getSimpleName() +
                ". Accepted values: " + Arrays.toString(enumClass.getEnumConstants()));
    }
}
