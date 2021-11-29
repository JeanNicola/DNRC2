package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity(name = Constants.ELECTRONIC_CONTACTS_TABLE)
@Getter
@Setter
public class ElectronicContacts {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "econ_seq"
    )
    @SequenceGenerator(
            name = "econ_seq",
            sequenceName = Constants.ELECTRONIC_SEQUENCE,
            allocationSize = 1
    )
    @Column(name = Constants.ELECTRONIC_ID)
    public BigDecimal electronicId;

    @Column(name = Constants.CUSTOMER_ID)
    private BigDecimal customerId;

    @Column(name = Constants.ELECTRONIC_VAL)
    public String electronicValue;

    @Column(name = Constants.ELECTRONIC_TYPE)
    public String electronicType;

    @Column(name = Constants.ELECTRONIC_NOTES)
    public String electronicNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.CUSTOMER_ID, referencedColumnName = Constants.CUSTOMER_ID, insertable = false, nullable = false, updatable = false)
    private Customer customer;

}
