package gov.mt.wris.models.IdClasses;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class OfficeCustomerId implements Serializable {
    private BigDecimal officeId;
    private BigDecimal contactId;
    private static final long serialVersionUID = 6373402L;
}
