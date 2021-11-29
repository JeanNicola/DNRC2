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
@Table(name = Constants.CUSTOMER_TABLE)
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cust_seq"
    )
    @SequenceGenerator(
            name = "cust_seq",
            sequenceName = Constants.CUSTOMER_SEQUENCE,
            allocationSize = 1
    )
    @Column(name = Constants.OWNER_CUSTOMER_ID)
    public BigDecimal customerId;

    @Column(name = Constants.LAST_NAME)
    public String lastName;

    @Column(name = Constants.FIRST_NAME)
    public String firstName;

    @Column(name = Constants.MIDDLE_INITIAL)
    public String middleInitial;

    @Column(name = Constants.SUFFIX)
    public String suffix;

    @Column(name = Constants.CONTACT_TYPE)
    public String contactType;

    @Column(name = Constants.CONTACT_STATUS)
    public String contactStatus;

    @OneToMany(mappedBy = "customer")
    public List<Owner> owners;

    @OneToMany(mappedBy = "customer")
    public List<CustomerXref> customerXref;

    @OneToMany(mappedBy = "customer")
    public List<MailingJobCustomer> mailingXrefs;

    @OrderBy("primaryMail DESC")
    @OneToMany(mappedBy = "customer")
    public List<Address> addresses;

    @OneToMany(mappedBy = "customer")
    public List<ElectronicContacts> electronicContacts;

    @ManyToOne(targetEntity = Reference.class)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.CONTACT_STATUS, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.CUSTOMER_STATUS_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference contactStatusValue;

    @ManyToOne(targetEntity = Reference.class)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.SUFFIX, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.CUSTOMER_SUFFIX_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference suffixValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.CONTACT_TYPE, referencedColumnName = Constants.CONTACT_TYPE, insertable = false, nullable = false, updatable = false)
    private CustomerTypes contactTypeValue;

}
