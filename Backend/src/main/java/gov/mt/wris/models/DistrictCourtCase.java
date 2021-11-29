package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = Constants.DISTRICT_COURT_TABLE)
@Getter
@Setter
public class DistrictCourtCase {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "dist_court_seq"
    )
    @SequenceGenerator(
            name = "dist_court_seq",
            sequenceName = Constants.DISTRICT_COURT_SEQUENCE,
            allocationSize = 1
    )
    @Column(name = Constants.DISTRICT_ID)
    public BigDecimal districtId;

    @Column(name = Constants.CASE_ID)
    public BigDecimal caseId;

    @Column(name = Constants.DISTRICT_CAUSE_NUMBER)
    public String causeNumber;

    @Column(name = Constants.DISTRICT_COURT)
    public Integer districtCourt;

    @Column(name = Constants.DISTRICT_JUDGE)
    public String judgeId;

    @ManyToOne(targetEntity = MasterStaffIndexes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.DISTRICT_JUDGE, referencedColumnName = Constants.MASTER_STAFF_INDEXES_DNRC_ID, insertable = false, updatable = false, nullable = false)
    private MasterStaffIndexes judge;

    @Column(name = Constants.COUNTIES_ID)
    public BigDecimal countyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.COUNTIES_ID, insertable = false, updatable = false, nullable = false)
    public County county;

    @Column(name = Constants.DISTRICT_SUPREME_CAUSE_NUMBER)
    public String supremeCourtCauseNumber;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = Constants.DISTRICT_ID, insertable = false, updatable = false, nullable = false)
    List<Event> events;

}
