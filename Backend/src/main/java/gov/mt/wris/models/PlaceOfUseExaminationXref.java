package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.PlaceOfUseExaminationXrefId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = Constants.POU_EXAMINATIONS_XREF)
@IdClass(PlaceOfUseExaminationXrefId.class)
@Getter
@Setter
public class PlaceOfUseExaminationXref {

    @Id
    @Column(name = Constants.POU_EXAMINATION_ID)
    private BigDecimal pexmId;

    @Id
    @Column(name = Constants.PLACE_OF_USE_ID)
    private BigDecimal placeId;

    @Id
    @Column(name = Constants.PURPOSE_ID)
    private BigDecimal purposeId;

    @Column(name = Constants.POU_EXAMINATIONS_ACRES)
    private BigDecimal acreage;

    @Column(name = Constants.AERIAL_ID)
    private BigDecimal aerialId;

    @Column(name = Constants.POU_SURVEY_ID)
    private BigDecimal surveyId;

    @ManyToOne(targetEntity = PlaceOfUse.class, fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = Constants.PLACE_OF_USE_ID, referencedColumnName = Constants.PLACE_OF_USE_ID, insertable = false, updatable = false, nullable = false),
            @JoinColumn(name = Constants.PURPOSE_ID, referencedColumnName = Constants.PURPOSE_ID, insertable = false, updatable = false, nullable = false)
    })
    public PlaceOfUse placeOfUse;

    @ManyToOne(targetEntity = PouExamination.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.POU_EXAMINATION_ID, referencedColumnName = Constants.POU_EXAMINATION_ID, insertable = false, updatable = false, nullable = false)
    public PouExamination pouExamination;

    @ManyToOne(targetEntity = AerialPhoto.class, fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = Constants.POU_EXAMINATION_ID, referencedColumnName = Constants.POU_EXAMINATION_ID, insertable = false, updatable = false, nullable = false),
            @JoinColumn(name = Constants.AERIAL_ID, referencedColumnName = Constants.AERIAL_ID, insertable = false, updatable = false, nullable = false)
    })
    public AerialPhoto aerialPhoto;

    @ManyToOne(targetEntity = WaterSurveyPouExamXref.class, fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = Constants.POU_EXAMINATION_ID, referencedColumnName = Constants.POU_EXAMINATION_ID, insertable = false, updatable = false, nullable = false),
            @JoinColumn(name = Constants.POU_SURVEY_ID, referencedColumnName = Constants.POU_SURVEY_ID, insertable = false, updatable = false, nullable = false)
    })
    public WaterSurveyPouExamXref waterSurveyPouExamXref;

}
