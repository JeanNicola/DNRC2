package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.OwnerId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = Constants.OWNER_TABLE)
@IdClass(OwnerId.class)
@Getter
@Setter
public class Owner {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "own_seq"
    )
    @SequenceGenerator(
        name = "own_seq",
        sequenceName = Constants.OWNER_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.OWNER_ID)
    public BigDecimal ownerId;

    @Id
    @Column(name = Constants.OWNER_CUSTOMER_ID)
    public BigDecimal customerId;

    @Column(name = Constants.OWNER_BEGIN_DATE)
    public LocalDate beginDate;

    @Column(name = Constants.OWNER_END_DATE)
    public LocalDate endDate;

    @Column(name = Constants.OWNER_ORIGINAL)
    public String originalOwner;

    @Column(name = Constants.OWNER_CONTRACT_FOR_DEED)
    public String contractForDeed;

    @Column(name = Constants.OWNER_RECEIVED_MAIL)
    public String receivedMail;

    @Column(name = Constants.OWNER_ORIGIN)
    public String origin;

    @ManyToOne(targetEntity = Application.class)
    @JoinColumn(name = Constants.APPLICATION_ID, referencedColumnName = Constants.APPLICATION_ID, updatable = false, nullable = false)
    public Application application;

    @ManyToOne(targetEntity = WaterRight.class)
    @JoinColumn(name = Constants.WATER_RIGHT_ID, referencedColumnName = Constants.WATER_RIGHT_ID, updatable = false, nullable = true)
    public WaterRight waterRight;

    @ManyToOne(targetEntity = Customer.class)
    @JoinColumn(name = Constants.OWNER_CUSTOMER_ID, referencedColumnName = Constants.OWNER_CUSTOMER_ID, nullable = false, insertable = false, updatable = false)
    public Customer customer;

    @OneToMany(targetEntity=Representative.class, mappedBy = "owner", cascade = CascadeType.ALL)
    // @Fetch(value = FetchMode.JOIN)
    public List<Representative> representatives;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.OWNER_CONTRACT_FOR_DEED, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula = @JoinFormula(value = "'" + Constants.CONTRACT_FOR_DEED_RLE_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    public Reference contractReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.OWNER_ORIGIN, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula = @JoinFormula(value = "'" + Constants.OWNER_ORIGIN_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    public Reference originReference;
}
