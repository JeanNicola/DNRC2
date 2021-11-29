package gov.mt.wris.models.IdClasses;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class WaterSurveyPouExamXrefId implements Serializable {
    private BigDecimal surveyId;
    private BigDecimal pexmId;
    private static final long serialVersionUID = 8675319L;
}