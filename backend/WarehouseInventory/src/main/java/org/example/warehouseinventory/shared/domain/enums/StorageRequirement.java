package org.example.warehouseinventory.shared.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.example.warehouseinventory.shared.api.exception.InvalidEnumException;

import java.util.Arrays;

public enum StorageRequirement implements JsonCreatable<StorageRequirement>{
    AMBIENT,
    REFRIGERATED,
    FROZEN,
    CONTROLLED_TEMP,
    DRY,
    HAZARDOUS,
    HIGH_SECURITY,
    FLAMMABLE,
    FRAGILE,
    OVERSIZED;

    @JsonCreator
    public static StorageRequirement fromValue(String value) {
        return JsonCreatable.fromValue(value, StorageRequirement.class);
    }

}
