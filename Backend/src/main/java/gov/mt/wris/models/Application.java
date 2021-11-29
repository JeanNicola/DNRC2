package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;


@Entity(name = Constants.APPLICATION_TABLE)
@Getter
@Setter
public class Application {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "app_seq"
    )
    @SequenceGenerator(
        name = "app_seq",
        sequenceName = Constants.APPLICATION_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.APPLICATION_ID)
    public BigDecimal id;

    @Column(name = Constants.APPLICATION_REGARDING_ID)
    public BigDecimal regardingId;

    @Column(name = Constants.APPLICATION_TYPE_CODE)
    public String typeCode;

    @Column(name = Constants.BASIN)
    public String basin;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Owner> applicants;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Event> events;

    @Column(name = Constants.OFFICE_ID)
    public BigDecimal officeId;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<StaffApplicationXref> staffApplicationXrefs;

    @ManyToOne(targetEntity = ApplicationType.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.APPLICATION_TYPE_CODE, referencedColumnName = Constants.APPLICATION_TYPE_CODE, insertable = false, updatable = false, nullable = false)
    public ApplicationType type;

    @Column(name = Constants.CHANGE_DESC)
    public String changeDesc;

    @Column(name = Constants.PAST_USE)
    public String pastUse;

    @Column(name = Constants.ADDITIONAL_INFO)
    public String additionalInfo;

    @Column(name = Constants.DISTANCE)
    public Long distance;

    @Column(name = Constants.DIRECTION)
    public String direction;

    @Column(name = Constants.FILING_FEE)
    public BigDecimal filingFee;

    @Column(name = Constants.FEE_STATUS)
    public String feeStatus;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.FEE_STATUS, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula=@JoinFormula(value = "'"+Constants.PAYMENT_APPLICATION_FEE_STATUS_DOMAIN+"'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference feeStatusReference;

    @Column(name = Constants.FEE_WAIVED)
    public String feeWaived;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.FEE_WAIVED, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula=@JoinFormula(value = "'"+Constants.YES_NO_DOMAIN+"'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference feeWaivedReference;

    @Column(name = Constants.FEE_OTHER)
    public String feeOther;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.FEE_OTHER, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula=@JoinFormula(value = "'"+Constants.YES_NO_DOMAIN+"'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference feeOtherReference;

    @Column(name = Constants.FEE_CGWA)
    public String feeCGWA;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.FEE_CGWA, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula=@JoinFormula(value = "'"+Constants.YES_NO_DOMAIN+"'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference feeCGWAReference;

    @Column(name = Constants.FEE_DISCOUNT)
    public String feeDiscount;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.FEE_DISCOUNT, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula=@JoinFormula(value = "'"+Constants.YES_NO_DOMAIN+"'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference feeDiscountReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(\n" +
            "SELECT e.evdt_id_seq \n" +
            "FROM wrd_event_dates e\n" +
            "WHERE ((e.EVTP_CD = 'PAMH' and APTP_CD in ('600P', '606P')) or\n" +
            "(e.EVTP_CD = 'FRMR' and APTP_CD not in ('600P', '606P')))\n" +
//            "WHERE (e.evtp_cd='FRMR')\n" +
            "AND e.APPL_ID_SEQ = APPL_ID_SEQ\n" +
            ")")
    public Event dateTimeReceivedEvent ;

    @ManyToMany
    @JoinTable(
        name = Constants.APPLICATION_VERSION_XREF_TABLE,
        joinColumns = { @JoinColumn(name = Constants.APPLICATION_ID) },
        inverseJoinColumns = { @JoinColumn(name = Constants.VERSION_ID),
                                @JoinColumn(name = Constants.VERSION_WATER_RIGHT_ID) }
    )
    Set<WaterRightVersion> waterRightVersions;

    // Water Right Summary Data
    @Column(name = Constants.MAXIMUM_VOLUME)
    BigDecimal maxVolume;

    @Column(name = Constants.MAXIMUM_ACRES)
    BigDecimal maxAcres;

    @Column(name = Constants.MAXIMUM_FLOW_RATE)
    BigDecimal maxFlowRate;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.FLOW_RATE_UNIT, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula=@JoinFormula(value = "'"+Constants.FLOW_RATE_UNIT_DOMAIN+"'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference flowRateReference;

    @Column(name = Constants.FLOW_RATE_UNIT)
    String flowRateUnit;

    @Column(name = Constants.NON_FILED_WATER_PROJECT)
    public String nonFiledWaterProject;

    @ManyToOne(targetEntity = Office.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.OFFICE_ID, referencedColumnName = Constants.OFFICE_ID, insertable = false, updatable = false)
    public Office office;

    @Column(name = Constants.PROCESSOR_OFFICE_ID)
    public BigDecimal processorOfficeId;

    @ManyToOne(targetEntity = Office.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.PROCESSOR_OFFICE_ID, referencedColumnName = Constants.OFFICE_ID, insertable = false, updatable = false)
    public Office processorOffice;

    @Column(name = Constants.PROCESSOR_STAFF_ID)
    public BigDecimal processorStaffId;

    @ManyToOne(targetEntity = MasterStaffIndexes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.PROCESSOR_STAFF_ID, referencedColumnName = Constants.MASTER_STAFF_INDEXES_DNRC_ID, insertable = false, updatable = false)
    public MasterStaffIndexes processorStaff;

    public void setApplicants(List<Owner> newOwners) {
        this.applicants = new ArrayList<Owner>(newOwners);
        if(newOwners != null) {
            for(Owner own : newOwners) {
                own.setApplication(this);
            }
        }
    }

    public void setEvents(List<Event> newEvents) {
        this.events = new ArrayList<Event>(newEvents);
        if(newEvents != null) {
            for(Event evnt : newEvents) {
                evnt.setApplication(this);
            }
        }
    }

    public void addEvent(Event newEvent) {
        List<Event> events = this.getEvents();
        events.add(newEvent);
        newEvent.setApplication(this);
    }

    public void addWaterRightVersion(WaterRightVersion newVersion) {
        this.waterRightVersions.add(newVersion);
        newVersion.applications.add(this);
    }
}
