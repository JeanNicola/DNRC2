package gov.mt.wris.models.IdClasses;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class WaterRighOwnshiptXrefId implements Serializable{
    private BigDecimal ownershipUpdateId;
    private BigDecimal waterRightId;
    private static final long serialVersionUID = 111111L;
}
