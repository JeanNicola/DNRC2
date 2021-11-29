package gov.mt.wris.models.IdClasses;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class OwnerId implements Serializable {
    private BigDecimal ownerId;
    private BigDecimal customerId;
    private static final long serialVersionUID = 512634L;
}
