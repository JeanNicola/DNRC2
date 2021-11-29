package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.WaterSurveyPouExamXrefId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = Constants.WATER_SURVEY_POU_EXAM_XREF)
@IdClass(WaterSurveyPouExamXrefId.class)
@Getter
@Setter
public class WaterSurveyPouExamXref {

    @Id
    @Column(name = Constants.POU_SURVEY_ID)
    private BigDecimal surveyId;

    @Id
    @Column(name = Constants.POU_EXAMINATION_ID)
    private BigDecimal pexmId;

    @ManyToOne(targetEntity = PouExamination.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.POU_EXAMINATION_ID, referencedColumnName = Constants.POU_EXAMINATION_ID, insertable = false, updatable = false, nullable = false)
    public PouExamination pouExamination;

    @ManyToOne(targetEntity = WaterResourceSurvey.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.POU_SURVEY_ID, referencedColumnName = Constants.POU_SURVEY_ID, insertable = false, updatable = false, nullable = false)
    public WaterResourceSurvey waterResourceSurvey;

    @OneToMany(mappedBy = "waterSurveyPouExamXref", fetch = FetchType.LAZY)
    public List<PlaceOfUseExaminationXref> placesOfUse;

}
