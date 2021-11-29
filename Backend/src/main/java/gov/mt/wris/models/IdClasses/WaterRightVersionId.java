package gov.mt.wris.models.IdClasses;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class WaterRightVersionId implements Serializable {
    private BigDecimal waterRightId;
    private BigDecimal version;
    private static final long serialVersionUID = 124323L;
}
