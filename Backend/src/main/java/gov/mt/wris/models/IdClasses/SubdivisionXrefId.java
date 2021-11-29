package gov.mt.wris.models.IdClasses;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class SubdivisionXrefId implements Serializable {
    public BigDecimal placeId;
    public String code;
    public BigDecimal purposeId;
    private static final long serialVersionUID = 8675305;
}
