package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.MailingJobWaterRightId;

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
@Getter
@Setter
@IdClass(MailingJobWaterRightId.class)
@Table(name = Constants.MAILING_JOB_WATER_RIGHT_XREF_TABLE)
public class MailingJobWaterRight {

    @Id
    @Column(name = Constants.MAILING_JOB_WATER_RIGHT_XREF_ID)
    public BigDecimal mailingJobId;

    @Id
    @Column(name = Constants.MAILING_JOB_WATER_RIGHT_XREF_WATER_RIGHT_ID)
    public BigDecimal waterRightId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.MAILING_JOB_WATER_RIGHT_XREF_WATER_RIGHT_ID, referencedColumnName = Constants.MAILING_JOB_WATER_RIGHT_XREF_WATER_RIGHT_ID, nullable = false, insertable = false, updatable = false)
    public WaterRight waterRight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.MAILING_JOB_WATER_RIGHT_XREF_ID, referencedColumnName = Constants.MAILING_JOB_WATER_RIGHT_XREF_ID, insertable = false, nullable = false, updatable = false)
    private MailingJob mailingJob;

}
