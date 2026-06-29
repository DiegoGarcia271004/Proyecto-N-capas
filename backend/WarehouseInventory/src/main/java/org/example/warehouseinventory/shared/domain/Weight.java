package org.example.warehouseinventory.shared.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.warehouseinventory.shared.domain.enums.WeightUnit;

import java.math.BigDecimal;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Weight {

    @Column(name = "weight_value")
    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    @Column(name = "weight_unit")
    private WeightUnit unit;

    /**
     * Constructor for an instance of Weight if the value is valid
     * @param value numerical value of the weight of the object
     * @param unit  unit of the product KG, LBS, OZ, G
     * @return
     */
    public static Weight of(BigDecimal value, WeightUnit unit) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Weight must be positive");
        Weight w = new Weight();
        w.value = value;
        w.unit = unit;
        return w;
    }
}
