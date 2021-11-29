package gov.mt.wris.models.IdClasses;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOfUseExaminationXrefId  implements Serializable {
    private BigDecimal pexmId;
    private BigDecimal placeId;
    private BigDecimal purposeId;
    private static final long serialVersionUID = 8675309L;
}
