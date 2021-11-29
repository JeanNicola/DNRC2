package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.DynamicUpdate;
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

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import java.math.BigDecimal;
import java.time.LocalDate;

@DynamicUpdate
@Entity
@Table(name = Constants.PERIOD_OF_USES_TABLE)
@Getter
@Setter
public class PeriodOfUse {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pous_seq"
    )
    @SequenceGenerator(
            name = "pous_seq",
            sequenceName = Constants.PERIOD_OF_USE_SEQUENCE,
            allocationSize = 1
    )
    @Column(name = Constants.PERIOD_OF_USE_ID)
    private BigDecimal periodId;

    @Formula("to_char(BGN_DT, 'MM/DD')")
    public String beginDateString;

    @Column(name = Constants.PERIOD_OF_USE_BEGIN_DATE)
    public LocalDate beginDate;

    @Formula("to_char(END_DT, 'MM/DD')")
    public String endDateString;

    @Column(name = Constants.PERIOD_OF_USE_END_DATE)
    public LocalDate endDate;

    @Column(name = Constants.ELEMENT_ORIGIN)
    private String elementOrigin;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.ELEMENT_ORIGIN, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.ORIGIN_DOMAIN+ "'", referencedColumnName = Constants.DOMAIN))
    })
    public Reference elementOriginReference;

    @Column(name = Constants.PURPOSE_ID)
    private BigDecimal purposeId;

    @ManyToOne(targetEntity = Purpose.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.PURPOSE_ID, referencedColumnName = Constants.PURPOSE_ID, insertable = false, updatable = false, nullable = false)
    public Purpose purpose;

    @Column(name = Constants.WATER_RIGHT_ID)
    private BigDecimal waterRightId;

    @Column(name = Constants.VERSION_ID)
    private BigDecimal versionId;

    @ManyToOne(targetEntity = WaterRightVersion.class, fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = Constants.WATER_RIGHT_ID, referencedColumnName = Constants.WATER_RIGHT_ID, insertable = false, updatable = false, nullable = false),
            @JoinColumn(name = Constants.VERSIONS_ID, referencedColumnName = Constants.VERSIONS_ID, insertable = false, updatable = false, nullable = false)
    })
    public WaterRightVersion waterRightVersion;

    @Column(name = Constants.PERIOD_OF_USE_FLOW_RATE)
    private BigDecimal flowRate;

    @Column(name = Constants.PERIOD_OF_USE_LEASE_YEAR)
    private String leaseYear;

    @Column(name = Constants.POINT_OF_DIVERSION_ID)
    public BigDecimal podId;

}
