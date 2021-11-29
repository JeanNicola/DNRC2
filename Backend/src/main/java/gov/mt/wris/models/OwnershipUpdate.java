package gov.mt.wris.models;


import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;

@Entity
@DynamicUpdate
@Table(name= Constants.WRD_OWNERSHIP_UPDATES_TABLE)
@Getter
@Setter
public class OwnershipUpdate {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "owner_update_seq"
    )
    @SequenceGenerator(
        name = "owner_update_seq",
        sequenceName = Constants.OWNERSHIP_UPDATE_SEQUENCE,
        allocationSize = 1
    )
    @Column(name= Constants.OWNR_UPDT_ID)
    public BigDecimal ownerUpdateId;

    @OneToMany(mappedBy = "ownershipUpdate", cascade = CascadeType.PERSIST)
    public Set<CustomerXref> customerXref;

    @Column(name= Constants.TRN_TYP)
    public String trnType;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.TRN_TYP, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.OWNERSHIP_TRANSFER_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference updateTypeValue;

    @Column(name= Constants.OWNERSHIP_UPDATE_DT_RECEIVED)
    public LocalDate dateReceived;

    @Column(name= Constants.OWNERSHIP_UPDATE_DT_PROCESSED)
    public LocalDate dateProcessed;

    @Column(name= Constants.OWNERSHIP_UPDATE_DT_TERMINATED)
    public LocalDate dateTerminated;

    @Column(name= Constants.OWNERSHIP_UPDATE_CREATED_BY)
    public String createdBy;

    @Column(name= Constants.OWNERSHIP_UPDATE_DTM_CREATED)
    public LocalDate dateCreated;

    @Column(name= Constants.OWNERSHIP_UPDATE_MOD_BY)
    public String modBy;

    @Column(name= Constants.OWNERSHIP_UPDATE_DTM_MOD)
    public LocalDate dateMod;

    @Column(name= Constants.OWNERSHIP_UPDATE_FEE_DUE)
    public BigDecimal feeDue;

    @Column(name= Constants.OWNERSHIP_UPDATE_FEE_STATUS)
    public String feeStatus;

    @Column(name= Constants.OWNERSHIP_UPDATE_FEE_DUE_LTR_SENT_DT)
    public LocalDate feeDueSentDate;

    @Column(name= Constants.OWNERSHIP_UPDATE_FEE_LETTER_WR)
    public String feeLetterWr;

    @Column(name= Constants.OWNERSHIP_UPDATE_NOTES)
    public String notes;

    @Column(name= Constants.OWNERSHIP_UPDATE_PENDING_DOR)
    public String pendingDor;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.OWNERSHIP_UPDATE_PENDING_DOR, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.YES_NO_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference pendingDorVal;

    @Column(name= Constants.RECEIVED_AS_608)
    public String receivedAs608;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.RECEIVED_AS_608, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.YES_NO_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference receivedAs608Val;

    @Column(name= Constants.OFFICE_ID)
    public BigDecimal officeId;

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

    @ManyToMany
    @JoinTable(
        name = Constants.WRD_WTR_RGT_OWNSHIP_UPDT_XREFS_TABLE,
        joinColumns = { @JoinColumn(name = Constants.OWNR_UPDT_ID) },
        inverseJoinColumns = { @JoinColumn(name = Constants.WATER_RIGHT_ID) }
    )
    public Set<WaterRight> waterRights;

    public void addCustomer(CustomerXref newCustomer) {
        if(this.customerXref == null) {
            this.customerXref = new HashSet<CustomerXref>();
        }
        this.customerXref.add(newCustomer);
        newCustomer.setOwnershipUpdate(this);
    }

}
