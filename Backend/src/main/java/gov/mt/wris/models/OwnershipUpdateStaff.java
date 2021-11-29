package gov.mt.wris.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.OWNERSHIP_UPDATE_STAFF_XREFS_TABLE)
@Getter
@Setter
public class OwnershipUpdateStaff {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "osxr_sequence"
    )
    @SequenceGenerator(
        name = "osxr_sequence",
        sequenceName = Constants.OWNERSHIP_UPDATE_STAFF_XREF_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.OWNERSHIP_UPDATE_STAFF_XREF_ID)
    private BigDecimal id;

    @Column(name = Constants.OWNR_UPDT_ID)
    private BigDecimal ownershipUpdateId;

    @Column(name = Constants.OWNERSHIP_UPDATE_STAFF_ID)
    private BigDecimal staffId;

    @ManyToOne(targetEntity = MasterStaffIndexes.class)
    @JoinColumn(name = Constants.OWNERSHIP_UPDATE_STAFF_ID, referencedColumnName = Constants.MASTER_STAFF_INDEXES_DNRC_ID, insertable = false, updatable = false, nullable = false)
    private MasterStaffIndexes staff;

    @Column(name = Constants.OWNERSHIP_UPDATE_STAFF_BEGIN_DATE)
    public LocalDate beginDate;

    @Column(name = Constants.OWNERSHIP_UPDATE_STAFF_END_DATE)
    public LocalDate endDate;

    @Column(name = Constants.OWNERSHIP_UPDATE_STAFF_CREATED_DATE)
    public LocalDateTime createdDate;
}
