package org.example.warehouseinventory.shared.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ProductCategory implements JsonCreatable<ProductCategory>{
    FOOD_PERISHABLE,
    FOOD_NON_PERISHABLE,
    BEVERAGES,
    FROZEN,

    PHARMACEUTICAL,
    MEDICAL_DEVICES,
    SUPPLEMENTS,

    PERSONAL_CARE,
    CLEANING_SUPPLIES,
    HOME_GOODS,

    ELECTRONICS,
    COMPONENTS,
    ACCESSORIES,

    APPAREL,
    FOOTWEAR,
    TEXTILES,

    RAW_MATERIALS,
    MACHINERY_PARTS,
    TOOLS,
    CHEMICALS,

    OFFICE_SUPPLIES,
    SEASONAL,
    PROMOTIONAL,
    OTHER;

    @JsonCreator
    public static ProductCategory fromValue(String value) {
        return JsonCreatable.fromValue(value, ProductCategory.class);
    }
}
