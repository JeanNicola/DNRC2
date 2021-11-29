package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name= Constants.CASE_ASSIGNMENTS_TABLE)
@Getter
@Setter
public class CaseAssignment {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "casn_seq"
    )
    @SequenceGenerator(
            name = "casn_seq",
            sequenceName = Constants.CASE_ASSIGNMENTS_SEQUENCE,
            allocationSize = 1
    )
    @Column(name=Constants.CASE_ASSIGNMENT_ID)
    public BigDecimal assignmentId;

    @Column(name=Constants.CASE_ID)
    public BigDecimal caseId;

    @ManyToOne(targetEntity = CourtCase.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.CASE_ID, referencedColumnName = Constants.CASE_ID, nullable = false, insertable = false, updatable = false)
    private CourtCase courtCase;

    @Column(name=Constants.ASSIGNMENT_CODE)
    public String assignmentTypeCode;

    @ManyToOne(targetEntity = CaseAssignmentType.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.ASSIGNMENT_CODE, referencedColumnName = Constants.ASSIGNMENT_CODE, nullable = false, insertable = false, updatable = false)
    private CaseAssignmentType caseAssignmentType;

    @Column(name=Constants.MASTER_STAFF_INDEXES_DNRC_ID)
    public String dnrcId;

    @ManyToOne(targetEntity = MasterStaffIndexes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.MASTER_STAFF_INDEXES_DNRC_ID, referencedColumnName = Constants.MASTER_STAFF_INDEXES_DNRC_ID, nullable = false, insertable = false, updatable = false)
    private MasterStaffIndexes masterStaffIndexes;

    @Column(name = Constants.CASE_ASSIGNMENT_BEGIN_DATE)
    public LocalDate beginDate;

    @Column(name = Constants.CASE_ASSIGNMENT_END_DATE)
    public LocalDate endDate;

}
