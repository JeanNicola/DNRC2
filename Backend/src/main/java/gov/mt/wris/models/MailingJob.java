package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;

import lombok.Getter;
import lombok.Setter;

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

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Vannara Houth
 */
@Entity
@Table(name = Constants.MAILING_JOB_TABLE)
@Getter
@Setter
public class MailingJob {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "job_sequence"
    )
    @SequenceGenerator(
        name = "job_sequence",
        sequenceName = Constants.MAILING_JOB_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.MAILING_JOB_ID)
    private BigDecimal id;

    @Column(name = Constants.MAILING_JOB_HEADER)
    private String header;

    @Column(name = Constants.APPLICATION_ID)
    private BigDecimal applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.APPLICATION_ID, referencedColumnName = Constants.APPLICATION_ID, insertable = false, updatable = false, nullable = false)
    private Application application;

    @Column(name = Constants.MAILING_DATE_GENERATED)
    private LocalDate dateGenerated;
}
