package gov.mt.wris.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.OfficeCustomerId;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.OFFICE_CONTACT_TABLE)
@IdClass(OfficeCustomerId.class)
@Getter
@Setter
public class OfficeCustomer {
    @Id
    @Column(name = Constants.CUSTOMER_ID)
    private BigDecimal contactId;

    @Id
    @Column(name = Constants.OFFICE_ID)
    private BigDecimal officeId;

    @Column(name = Constants.OFFICE_DEFAULT_PARTY)
    private String defaultParty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.CUSTOMER_ID, referencedColumnName = Constants.CUSTOMER_ID, insertable = false, updatable = false, nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.OFFICE_ID, referencedColumnName = Constants.OFFICE_ID, insertable = false, updatable = false, nullable = false)
    private Office office;
}
