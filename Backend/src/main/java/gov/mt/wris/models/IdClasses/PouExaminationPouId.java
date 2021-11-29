package gov.mt.wris.models.IdClasses;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PouExaminationPouId implements Serializable {
    private BigDecimal placeId;
    private BigDecimal purposeId;
    private BigDecimal pexmId;
    private static final long serialVersionUID = 8675319L;
}
