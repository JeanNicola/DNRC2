package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = Constants.EXAMINATIONS_TABLE)
@Getter
@Setter
public class Examination {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "exam_id_seq"
    )
    @SequenceGenerator(
            name = "exam_id_seq",
            sequenceName = Constants.EXAMINATION_SEQUENCE,
            allocationSize = 1
    )
    @Column(name = Constants.EXAMINATION_ID)
    public BigDecimal examinationId;

    @Column(name = Constants.EXAMINATION_BEGIN_DATE)
    public LocalDate beginDate;

    @Column(name = Constants.EXAMINATION_END_DATE)
    public LocalDate endDate;

    @Column(name = Constants.PURPOSE_ID)
    public BigDecimal purposeId;

    @ManyToOne(targetEntity = Purpose.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.PURPOSE_ID, referencedColumnName = Constants.PURPOSE_ID, insertable = false, updatable = false, nullable = false)
    public Purpose purpose;

    @Column(name = Constants.EXAMINATION_EXAMINER)
    public String examiner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.EXAMINATION_EXAMINER, referencedColumnName = Constants.MASTER_STAFF_INDEXES_DNRC_ID, nullable = false, updatable = false, insertable = false)
    public MasterStaffIndexes staff;

    @Column(name = Constants.EXAMINATION_ERROR_CHECK_FLAG)
    public String errorCheckFlag;

    @OneToMany(mappedBy = "examination", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<PouExamination> pouExaminations;

}
