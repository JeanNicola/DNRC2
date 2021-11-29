package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = Constants.POU_EXAMINATIONS_TABLE)
@Getter
@Setter
public class PouExamination {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pexm_id_seq"
    )
    @SequenceGenerator(
            name = "pexm_id_seq",
            sequenceName = Constants.POU_EXAMINATION_SEQUENCE,
            allocationSize = 1
    )
    @Column(name = Constants.POU_EXAMINATION_ID)
    public BigDecimal pexmId;

    @Column(name = Constants.EXAMINATION_ID)
    public BigDecimal examinationId;

    @ManyToOne(targetEntity = Examination.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.EXAMINATION_ID, referencedColumnName = Constants.EXAMINATION_ID, insertable = false, updatable = false, nullable = false)
    public Examination examination;

    @OneToMany(mappedBy = "pouExamination", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<PlaceOfUseExaminationXref> placeOfUseExaminations;

    @OneToMany(mappedBy = "pouExamination", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<AerialPhoto> aerialPhotos;

    @OneToMany(mappedBy = "pouExamination", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<PouExamUsgsMapXref> usgsXrefs;

    @OneToMany(mappedBy = "pouExamination", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<WaterSurveyPouExamXref> waterSurveyXrefs;

    @Column(name = Constants.POU_EXAMINATION_INVESTIGATION_DATE)
    public LocalDate investigationDate;

    @Column(name = Constants.POU_EXAMINATION_DATA_SRC_TYP)
    public String sourceType;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.POU_EXAMINATION_DATA_SRC_TYP, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.DATA_SOURCE_TYPE + "'", referencedColumnName = Constants.DOMAIN))
    })
    public Reference sourceTypeReference;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = Constants.POU_EXAMINATION_ID, referencedColumnName = Constants.POU_EXAMINATION_ID, insertable = false, updatable = false, nullable = false)
    public DataSourceTotalAcres dataSourceTotalAcres;

}
