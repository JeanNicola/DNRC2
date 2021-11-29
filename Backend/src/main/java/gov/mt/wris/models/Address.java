package gov.mt.wris.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

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

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@DynamicUpdate
@Table(name = Constants.ADDRESS_TABLE)
@Getter
@Setter
public class Address {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "address_seq"
    )
    @SequenceGenerator(
        name = "address_seq",
        sequenceName = Constants.ADDRESS_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.ADDRESS_ID)
    private BigInteger addressId;

    @Column(name = Constants.ZIP_CODE_ID)
    private BigInteger zipCodeId;

    @Column(name = Constants.CUSTOMER_ID)
    private BigInteger customerId;

    @Column(name = Constants.PRIMARY_MAIL)
    private String primaryMail;

    @Column(name = Constants.ADDRESS_LINE_1)
    private String addressLine1;

    @Column(name = Constants.ADDRESS_LINE_2)
    private String addressLine2;

    @Column(name = Constants.ADDRESS_LINE_3)
    private String addressLine3;

    @Column(name = Constants.PL_FOUR)
    private String plFour;

    @Column(name = Constants.FOREIGN_ADDRESS)
    private String foreignAddress;

    @Column(name = Constants.FOREIGN_POSTAL)
    private String foreignPostal;

    @Column(name = Constants.DATE_CREATED, updatable = false, insertable = false)
    private LocalDate dateCreated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(\n" +
            "SELECT m.dnrc_id \n" +
            "FROM WRD_MASTER_STAFF_INDEXES m\n" +
            "WHERE (m.END_DT is null or m.END_DT > DTM_CREATED)\n" +
            "AND m.BGN_DT <= DTM_CREATED\n" +
            "AND m.C_NO = CREATED_BY\n" +
            ")")
    public MasterStaffIndexes createdByName;

    @Column(name = Constants.CREATED_BY, updatable = false, insertable = false)
    private String createdBy;

    @Column(name = Constants.DTM_MOD, updatable = false, insertable = false)
    private LocalDate dateModified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(\n" +
            "SELECT m.dnrc_id\n" +
            "FROM WRD_MASTER_STAFF_INDEXES m\n" +
            "WHERE (m.END_DT is null or m.END_DT > DTM_MOD)\n" +
            "AND m.BGN_DT <= DTM_MOD\n" +
            "AND m.C_NO = MOD_BY\n" +
            ")")
    public MasterStaffIndexes modifiedByName;

    @Column(name = Constants.MOD_BY, updatable = false, insertable = false)
    private String modifiedBy;

    @Column(name = Constants.MODIFIED_REASON)
    private String modifiedReason;

    @Column(name = Constants.UNRESOLVED_FLAG)
    private String unresolvedFlag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.CUSTOMER_ID, referencedColumnName = Constants.CUSTOMER_ID, insertable = false, nullable = false, updatable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.ZIP_CODE_ID, referencedColumnName = Constants.ZIP_CODE_ID, insertable = false, nullable = false, updatable = false)
    private ZipCode zipCode;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.FOREIGN_ADDRESS, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.YES_NO_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference foreignAddressValue;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.PRIMARY_MAIL, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.YES_NO_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference primaryMailValue;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.UNRESOLVED_FLAG, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.YES_NO_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference unresolvedFlagValue;

    @Column(name = Constants.POINT_OF_DIVERSION_ID)
    public BigDecimal pointOfDiversionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.POINT_OF_DIVERSION_ID, insertable = false, updatable = false, nullable = false)
    public PointOfDiversion pointOfDiversion;
}
