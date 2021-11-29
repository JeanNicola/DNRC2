package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = Constants.WATER_COURT_CASE_TYPES_TABLE)
@Getter
@Setter
public class CourtCase {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "case_seq"
    )
    @SequenceGenerator(
            name = "case_seq",
            sequenceName = Constants.WATER_COURT_CASE_TYPES_SEQUENCE,
            allocationSize = 1
    )
    @Column(name = Constants.CASE_ID)
    public BigDecimal id;

    @Column(name = Constants.CASE_NO)
    public String caseNumber;

    @Column(name = Constants.CITY_ID)
    public BigDecimal cityId;

    @Column(name = Constants.CASE_STATUS_CODE)
    public String caseStatusCode;

    @ManyToOne(targetEntity = CaseStatus.class)
    @JoinColumn(name = Constants.CASE_STATUS_CODE, referencedColumnName = Constants.CASE_STATUS_CODE, insertable = false, updatable = false, nullable = false)
    public CaseStatus caseStatus;

    @Column(name = Constants.CASE_TYPE_CODE)
    public String caseTypeCode;

    @ManyToOne(targetEntity = CaseType.class)
    @JoinColumn(name = Constants.CASE_TYPE_CODE, referencedColumnName = Constants.CASE_TYPE_CODE, insertable = false, updatable = false, nullable = false)
    public CaseType caseType;

    @Column(name = Constants.DECREE_ID)
    public BigDecimal decreeId;

    @Column(name = Constants.OFFC_ID_SEQ)
    public BigDecimal officeId;

    @ManyToOne(targetEntity = Office.class)
    @JoinColumn(name = Constants.OFFC_ID_SEQ, referencedColumnName = Constants.OFFC_ID_SEQ, insertable = false, updatable = false, nullable = false)
    public Office office;

    @Column(name = Constants.SIGNIFICANT_CASE)
    public String significantCase;

    @ManyToOne(targetEntity = Reference.class)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.SIGNIFICANT_CASE, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.YES_NO_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference significantCaseReference;

    @Column(name = Constants.CASE_COMMENTS)
    public String caseComments;

    @ManyToOne(targetEntity = Decree.class)
    @JoinColumn(name = Constants.DECREE_ID, referencedColumnName = Constants.DECREE_ID, insertable = false, updatable = false, nullable = false)
    public Decree decree;

    @OneToMany(mappedBy = "courtCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<CaseAssignment> caseAssignments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.CASE_ID, referencedColumnName = Constants.CASE_ID, insertable = false, updatable = false, nullable = false)
    public CourtCaseAssignToNa courtCaseAssignToNa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.CASE_ID, referencedColumnName = Constants.CASE_ID, insertable = false, updatable = false, nullable = false)
    public CourtCaseAssignToWc courtCaseAssignToWc;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = Constants.CASE_ID, referencedColumnName = Constants.CASE_ID, insertable = false, updatable = false, nullable = false)
    public CourtCaseDecreeDetail decreeIssuedDate;

    /** Court Cases to Applications is designed in database 1-to-n but this implementation follows current business requirements/practice of 1-to-1 **/
    @OneToOne(targetEntity = CaseApplicationXref.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.CASE_ID, referencedColumnName = Constants.CASE_ID, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "(SELECT XREF.APPL_ID_SEQ FROM WRD_CASE_APPLICATION_XREFS XREF WHERE XREF.CASE_ID_SEQ = CASE_ID_SEQ AND ROWNUM = 1)", referencedColumnName = Constants.APPLICATION_ID))
    })
    public CaseApplicationXref caseApplicationXrefs;

    @OneToMany(targetEntity = DistrictCourt.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = Constants.CASE_ID, referencedColumnName = Constants.CASE_ID, insertable = false, updatable = false, nullable = false)
    List<DistrictCourt> districtCourts;

    @OneToMany(targetEntity = CaseLodging.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = Constants.CASE_ID, referencedColumnName = Constants.CASE_ID, insertable = false, updatable = false, nullable = false)
    List<CaseLodging> caseLodgings;

    @OneToMany(targetEntity = CaseVehicle.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = Constants.CASE_ID, referencedColumnName = Constants.CASE_ID, insertable = false, updatable = false, nullable = false)
    List<CaseVehicle> caseVehicles;

    @OneToMany(targetEntity = CaseSummary.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = Constants.CASE_ID, referencedColumnName = Constants.CASE_ID, insertable = false, updatable = false, nullable = false)
    List<CaseSummary> caseSummaries;

    @OneToMany(targetEntity = Event.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = Constants.CASE_ID, referencedColumnName = Constants.CASE_ID, insertable = false, updatable = false, nullable = false)
    List<Event> events;

    @OneToMany(targetEntity = CaseSchedule.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = Constants.CASE_ID, referencedColumnName = Constants.CASE_ID, insertable = false, updatable = false, nullable = false)
    List<CaseSchedule> caseSchedules;

    @OneToMany(targetEntity = CourtCaseVersionXref.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = Constants.CASE_ID, referencedColumnName = Constants.CASE_ID, insertable = false, updatable = false, nullable = false)
    List<CourtCaseVersionXref> courtCaseVersionXrefs;
}

