package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = Constants.EVENT_TABLE)
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "event_seq"
    )
    @SequenceGenerator(
            name = "event_seq",
            sequenceName = Constants.EVENT_SEQUENCE,
            allocationSize = 1
    )
    @Column(name = Constants.EVENT_ID)
    public BigDecimal eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.EVENT_TYPE_CODE, referencedColumnName = Constants.EVENT_TYPE_CODE, nullable = false, updatable = false, insertable = false)
    public EventType eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(\n" +
            "SELECT m.dnrc_id\n" +
            "FROM WRD_MASTER_STAFF_INDEXES m\n" +
            "WHERE (m.END_DT is null or m.END_DT > DTM_MOD)\n" +
            "AND m.BGN_DT <= DTM_MOD\n" +
            "AND m.C_NO = MOD_BY\n" +
            ")")
    public MasterStaffIndexes modifiedByName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(\n" +
            "SELECT m.dnrc_id \n" +
            "FROM WRD_MASTER_STAFF_INDEXES m\n" +
            "WHERE (m.END_DT is null or m.END_DT > DTM_CREATED)\n" +
            "AND m.BGN_DT <= DTM_CREATED\n" +
            "AND m.C_NO = CREATED_BY\n" +
            ")")
    public MasterStaffIndexes createdByName;

    @Column(name = Constants.EVENT_TYPE_CODE)
    public String eventTypeCode;

    @Column(name = Constants.EVENT_COMMENT)
    public String eventComment;

    @Column(name = Constants.EVENT_DATE)
    public LocalDateTime eventDate;

    @Column(name = Constants.EVENT_RESPONSE_DUE)
    public LocalDateTime responseDueDate;

    @Column(name = Constants.APPLICATION_ID, insertable = false, updatable = false)
    public BigDecimal appId;

    @Column(name = Constants.EVENT_CREATED_BY)
    public String createdBy;

    @Column(name = Constants.EVENT_MODIFIED_BY)
    public String modifiedBy;

    @Column(name = Constants.EVENT_MODIFIED_DATE)
    public LocalDate modifiedDate;

    @Column(name = Constants.EVENT_CREATED_DATE)
    public LocalDate createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.APPLICATION_ID, referencedColumnName = Constants.APPLICATION_ID, nullable = false, updatable = false)
    public Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.DECREE_ID, referencedColumnName = Constants.DECREE_ID, nullable = false, updatable = false)
    public Decree decree;

    @Column(name = Constants.CASE_ID)
    public BigDecimal caseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.CASE_ID, referencedColumnName = Constants.CASE_ID, nullable = false, updatable = false, insertable = false)
    public CourtCase courtCase;

    @Column(name = Constants.DISTRICT_ID)
    public BigDecimal districtId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.DISTRICT_ID, referencedColumnName = Constants.DISTRICT_ID, nullable = false, updatable = false, insertable = false)
    public DistrictCourtCase districtCourtCase;

}
