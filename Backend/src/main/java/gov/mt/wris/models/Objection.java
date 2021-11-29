package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Cesar.Zamorano
 *
 */
@Entity
@Table(name = Constants.OBJECTIONS_TABLE)
@Getter
@Setter
public class Objection {

	@Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "objection_seq"
    )
    @SequenceGenerator(
            name = "objection_seq",
            sequenceName = Constants.OBJECTIONS_SEQUENCE,
            allocationSize = 1
    )
	@Column(name = Constants.OBJECTIONS_ID)
	private BigDecimal id;

    @Column(name = Constants.APPLICATION_ID)
    public BigDecimal applicationId;

    @Column(name = Constants.OBJECTIONS_TYPE)
    private String type;

	@ManyToOne(targetEntity = Reference.class)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.OBJECTIONS_TYPE, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula=@JoinFormula(value = "'"+Constants.OBJECTIONS_TYPE_DOMAIN+"'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference typeReference;

    @Column(name = Constants.OBJECTIONS_LATE)
    private String objectionLate;

	@ManyToOne(targetEntity = Reference.class)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.OBJECTIONS_LATE, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula=@JoinFormula(value = "'"+Constants.YES_NO_DOMAIN+"'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference lateReference;

    @Column(name = Constants.OBJECTIONS_STATUS)
    private String status;

	@ManyToOne(targetEntity = Reference.class)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.OBJECTIONS_STATUS, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula=@JoinFormula(value = "'"+Constants.OBJECTIONS_STATUS_DOMAIN+"'", referencedColumnName = Constants.DOMAIN))
    })
	private Reference statusReference;

	@Column(name = Constants.OBJECTIONS_DATE_RECEIVED)
	private LocalDate dateReceived;

    @Formula(value = "CASE WHEN STATUS = 'VALD' THEN 0 ELSE 1 END")
    private Boolean isValid;

    @Formula(value = "CASE WHEN STATUS = 'INVD' THEN 0 ELSE 1 END")
    private Boolean isInvalid;

    @Formula(value = "CASE WHEN STATUS = 'OPEN' THEN 0 ELSE 1 END")
    private Boolean isOpen;

    @Column(name = Constants.VERSION_WATER_RIGHT_ID)
    private BigDecimal waterRightId;

    @Column(name = Constants.VERSIONS_ID)
    private BigDecimal versionId;

    @Column(name = Constants.DECREE_ID)
    private BigDecimal decreeId;

    @ManyToOne(targetEntity = Decree.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.DECREE_ID, referencedColumnName = Constants.DECREE_ID, insertable = false, updatable = false, nullable = false)
    public Decree decree;

    @ManyToOne(targetEntity = WaterRightVersion.class, fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = Constants.VERSION_WATER_RIGHT_ID, referencedColumnName = Constants.VERSION_WATER_RIGHT_ID, insertable = false, updatable = false, nullable = false),
        @JoinColumn(name = Constants.VERSIONS_ID, referencedColumnName = Constants.VERSIONS_ID, insertable = false, updatable = false, nullable = false)
    })
    public WaterRightVersion waterRightVersion;

    @Column(name = Constants.CASE_ID)
    private BigDecimal caseId;

}
