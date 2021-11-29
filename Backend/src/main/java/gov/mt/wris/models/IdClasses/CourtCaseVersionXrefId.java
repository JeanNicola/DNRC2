package gov.mt.wris.models.IdClasses;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CourtCaseVersionXrefId implements Serializable {
    private BigDecimal waterRightId;
    private BigDecimal versionId;
    private BigDecimal caseId;
    private static final long serialVersionUID = 8675309L;
}
