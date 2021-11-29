package gov.mt.wris.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name=Constants.CUSTM_OWNERSHIP_UPDT_XREFS_TABLE)
@Getter
@Setter
public class CustomerXref {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "customer_xref_seq"
    )
    @SequenceGenerator(
        name = "customer_xref_seq",
        sequenceName = Constants.OWNERSHIP_UPDATE_CUSTOMERS_SEQUENCE,
        allocationSize = 1
    )
    @Column(name= Constants.COUX_ID_SEQ)
    public BigDecimal couxIdSeq;

    @Column(name = Constants.OWNR_UPDT_ID)
    public BigDecimal ownerUpdateId;

    @ManyToOne(targetEntity = OwnershipUpdate.class)
    @JoinColumn(name = Constants.OWNR_UPDT_ID, referencedColumnName = Constants.OWNR_UPDT_ID, insertable = false, updatable = false, nullable = false)
    public OwnershipUpdate ownershipUpdate;

    @Column(name= Constants.ROLE)
    public String role;

    @Column(name= Constants.CUST_XREF_END_DT)
    public LocalDate endDate;

    @Column(name= Constants.STR_DT)
    public LocalDate strDate;

    @Column(name= Constants.CHAIN_OF_TTL)
    public String chainOfTtl;

    @Column(name= Constants.CONTT_FOR_DEED)
    public String conttForDeed;
    
    @ManyToOne(targetEntity = Reference.class)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.CONTT_FOR_DEED, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.CONTRACT_FOR_DEED_RLE_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference conttForDeedValue;

    @Column(name= Constants.SEND_MAIL)
    public String sendMail;

    @Column(name = Constants.CUST_XREF_ID_SEQ)
    public BigDecimal customerId;

    @ManyToOne(targetEntity = Customer.class)
    @JoinColumn(name = Constants.CUST_XREF_ID_SEQ, referencedColumnName = Constants.CUST_XREF_ID_SEQ, nullable = false, insertable = false, updatable = false)
    public Customer customer;

    @Column(name= Constants.CUST_XREF_CREATED_BY)
    public String createdBy;

    @Column(name= Constants.CUST_XREF_DTM_CREATED)
    public LocalDate dateCreated;

    @Column(name= Constants.CUST_XREF_MOD_BY)
    public String modBy;

    @Column(name= Constants.CUST_XREF_DTM_MOD)
    public LocalDate dateMod;

    @Column(name= Constants.DOR_CUST_TYPE)
    public String dorCustType;

}
