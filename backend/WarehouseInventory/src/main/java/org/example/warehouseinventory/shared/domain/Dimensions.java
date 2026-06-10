package org.example.warehouseinventory.shared.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.warehouseinventory.shared.domain.enums.DimensionUnit;

import java.math.BigDecimal;
import java.util.stream.Stream;

@Embeddable
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dimensions {

    @Column(name = "dim_height")
    private BigDecimal height;

    @Column(name = "dim_width")
    private BigDecimal width;

    @Column(name = "dim_depth")
    private BigDecimal depth;

    @Column(name = "dimension_unit")
    @Enumerated(EnumType.STRING)
    private DimensionUnit unit;

    /**
     * Constructor for an instance of Dimensions if the dimensions are valid
     * @param height receives the height of the product
     * @param width receives the width of the product
     * @param depth receives the depth of the product
     * @param dimensionUnit receives the dimension unit for the dimensions of the product KG, LBS, OZ, G
     * @return an instance of Dimensions
     */
    public static Dimensions of(BigDecimal height, BigDecimal width, BigDecimal depth, DimensionUnit dimensionUnit){
        if (Stream.of(height, width, depth).anyMatch(v -> v == null || v.compareTo(BigDecimal.ZERO) < 0))
            throw new IllegalArgumentException("Dimensions must be positive");
        Dimensions d = new Dimensions();

        d.height = height;
        d.width = width;
        d.depth = depth;
        d.unit = dimensionUnit;

        return d;
    }

    /**
     * It does what you think it does
     * @return the volume of the product
     */
    public BigDecimal volume() {
        return height.multiply(width).multiply(depth);
    }
}
