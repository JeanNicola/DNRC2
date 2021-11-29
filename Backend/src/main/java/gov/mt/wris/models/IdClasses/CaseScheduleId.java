package gov.mt.wris.models.IdClasses;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CaseScheduleId implements Serializable {
    private BigDecimal scheduleId;
    private BigDecimal caseId;
    private static final long serialVersionUID = 8675329L;
}
