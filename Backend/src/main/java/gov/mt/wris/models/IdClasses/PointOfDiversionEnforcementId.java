package gov.mt.wris.models.IdClasses;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PointOfDiversionEnforcementId implements Serializable {
    public BigDecimal pointOfDiversionId;
    public String enforcementId;
    public String enforcementNumber;
    public static final long serialVersionUID = 234634L;
}
