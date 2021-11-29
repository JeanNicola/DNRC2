package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.MailingJobCustomerId;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name=Constants.MAILING_JOB_XREF_TABLE)
@IdClass(MailingJobCustomerId.class)
@Getter
@Setter
public class MailingJobCustomer {
    @Id
    @Column(name=Constants.MAILING_JOB_XREF_ID)
    public BigDecimal mailingJobId;

    @Id
    @Column(name=Constants.MAILING_JOB_XREF_CONTACT_ID)
    public BigDecimal contactId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.MAILING_JOB_XREF_ID, referencedColumnName = Constants.MAILING_JOB_ID, nullable = false, insertable = false, updatable = false)
    public MailingJob mailingJob;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.MAILING_JOB_XREF_CONTACT_ID, referencedColumnName = Constants.OWNER_CUSTOMER_ID, insertable = false, nullable = false, updatable = false)
    private Customer customer;

}
