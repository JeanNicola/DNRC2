package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = Constants.WATER_RIGHT_TABLE)
public class WaterRight {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "water_right_seq"
    )
    @SequenceGenerator(
        name = "water_right_seq",
        sequenceName = Constants.WATER_RIGHT_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.WATER_RIGHT_ID)
    public BigDecimal waterRightId;

    @Column(name = Constants.WATER_RIGHT_BASIN)
    public String basin;

    @Column(name = Constants.WATER_RIGHT_NUMBER)
    public BigDecimal waterRightNumber;

    @Column(name = Constants.WATER_RIGHT_EXT)
    public String ext;

    @Column(name = Constants.WATER_RIGHT_CON_DIST_NO)
    public String conDistNo;

    @Column(name = Constants.WATER_RIGHT_CON_DIST_DATE)
    public LocalDateTime conDistDate;

    @Column(name = Constants.WATER_RESERVATION_ID)
    public BigDecimal waterReservationId;

    @Column(name = Constants.WATER_RIGHT_SUB_BASIN)
    public String subBasin;

    @Column(name = Constants.DIVIDED_OWNSHP)
    public String dividedOwnship;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.DIVIDED_OWNSHP, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.YES_NO_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference dividedOwnshipValue;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.WATER_RIGHT_SEVERED, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.YES_NO_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference severedValue;

    @Column(name = Constants.SEVERED)
    public String severed;

    @Column(name = Constants.WATER_RIGHT_TYPE_CODE)
    public String waterRightTypeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.WATER_RIGHT_TYPE_CODE, referencedColumnName = Constants.WATER_RIGHT_TYPE_CODE, nullable = false, updatable = false, insertable = false)
    public WaterRightType waterRightType;

    @Column(name = Constants.WATER_RIGHT_STATUS_CODE)
    public String waterRightStatusCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.WATER_RIGHT_STATUS_CODE, referencedColumnName = Constants.WATER_RIGHT_STATUS_CODE, nullable = false, updatable = false, insertable = false)
    public WaterRightStatus waterRightStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.ORIGINAL_WATER_RIGHT_ID, referencedColumnName = Constants.WATER_RIGHT_ID)
    public WaterRight originalWaterRight;

    @OneToMany(mappedBy = "waterRight")
    public List<MailingJobWaterRight> mailingJobRefs;

    @Column(name = Constants.OFFICE_ID)
    public BigDecimal officeId;

    @ManyToOne(targetEntity = Office.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.OFFICE_ID, referencedColumnName = Constants.OFFICE_ID, insertable = false, updatable = false, nullable = false)
    public Office responsibleOffice;

    @Column(name = Constants.WATER_RIGHT_CREATE_DATE)
    public LocalDate createdDate;

    @ManyToMany // WATER_RIGHT_ID
    @JoinTable(
            name = Constants.WRD_WTR_RGT_OWNSHIP_UPDT_XREFS_TABLE,
            joinColumns = { @JoinColumn(name = Constants.WATER_RIGHT_ID) },
            inverseJoinColumns = { @JoinColumn(name = Constants.OWNR_UPDT_ID) }
    )
    public Set<OwnershipUpdate> ownershipUpdate;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = Constants.WATER_RIGHT_ID, referencedColumnName = Constants.WATER_RIGHT_ID, insertable = false, updatable = false, nullable = false)
    public WaterRightGeocodeValidity validity;

    @LazyCollection(LazyCollectionOption.EXTRA)
    @OneToMany(mappedBy = "waterRight", fetch = FetchType.LAZY)
    public List<WaterRightVersion> versions;

    @Column(name = Constants.SUBCOMPACT_ID)
    public BigDecimal subcompactId;

    @ManyToOne(targetEntity = Subcompact.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.SUBCOMPACT_ID, referencedColumnName = Constants.SUBCOMPACT_ID, insertable = false, updatable = false, nullable = false)
    public Subcompact subcompact;

    @OneToMany(mappedBy = "waterRight", cascade = CascadeType.ALL)
    public List<Owner> owners;

    public void setOwners(List<Owner> newOwners) {
        this.owners = newOwners;
        for(Owner own: newOwners) {
            own.setWaterRight(this);
        }
    }
}
