package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = Constants.WATER_RESOURCE_SURVEYS_TABLE)
@Getter
@Setter
public class WaterResourceSurvey {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "wrsy_id_seq"
    )
    @SequenceGenerator(
            name = "wrsy_id_seq",
            sequenceName = Constants.WATER_SURVEY_SEQUENCE,
            allocationSize = 1
    )
    @Column(name = Constants.POU_SURVEY_ID)
    private BigDecimal surveyId;

    @Column(name = Constants.WATER_SURVEY_YR)
    public BigDecimal yr;

    @Column(name = Constants.COUNTIES_ID)
    private BigDecimal countyId;

    @ManyToOne(targetEntity = County.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.COUNTIES_ID, referencedColumnName = Constants.COUNTIES_ID, insertable = false, updatable = false, nullable = false)
    public County county;

}
