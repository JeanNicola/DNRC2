package gov.mt.wris.models.IdClasses;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CaseApplicationXrefId implements Serializable{
    private BigDecimal caseId;
    private BigDecimal applicationId;
    private static final long serialVersionUID = 3451221L;
}
