package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.WaterRightVersionId;
import gov.mt.wris.utils.BooleanConverter;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@DynamicUpdate
@IdClass(WaterRightVersionId.class)
@Table(name = Constants.VERSION_TABLE)
public class WaterRightVersion {
    @Id
    @Column(name = Constants.VERSION_ID)
    public BigDecimal version;

    @Id
    @Column(name = Constants.VERSION_WATER_RIGHT_ID)
    public BigDecimal waterRightId;

    @Column(name = Constants.WATER_RIGHT_STATUS_CODE)
    public String statusCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.WATER_RIGHT_STATUS_CODE, referencedColumnName = Constants.WATER_RIGHT_STATUS_CODE, nullable = false, updatable = false, insertable = false)
    public WaterRightStatus versionStatus;

    @Column(name = Constants.VERSION_TYPE_CODE)
    public String typeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.VERSION_TYPE_CODE, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula = @JoinFormula(value = "'" + Constants.VERSION_TYPE_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference typeReference;

    @Column(name = Constants.VERSION_SCANNED)
    @Convert(converter = BooleanConverter.class)
    public Boolean scanned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.VERSION_WATER_RIGHT_ID, referencedColumnName = Constants.WATER_RIGHT_ID, nullable = false, updatable = false, insertable = false)
    public WaterRight waterRight;

    @ManyToMany(mappedBy = "waterRightVersions")
    public Set<Application> applications;

    @Column(name = Constants.VERSION_OPER_AUTHORITY)
    public LocalDate operatingAuthority;

    @Column(name = Constants.MAXIMUM_VOLUME)
    public BigDecimal maximumVolume;

    @Column(name = Constants.VERSION_VOLUME_DESCRIPTION)
    public String volumeDescription;
    
    @Column(name = Constants.MAXIMUM_FLOW_RATE)
    public BigDecimal maximumFlowRate;

    @Column(name = Constants.FLOW_RATE_UNIT)
    public String flowRateUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.FLOW_RATE_UNIT, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula = @JoinFormula(value = "'" + Constants.FLOW_RATE_UNIT_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    public Reference flowRateUnitReference;

    @Column(name = Constants.MAXIMUM_ACRES)
    public BigDecimal maximumAcres;

    @Column(name = Constants.VERSION_PRIORITY_DATE)
    public LocalDateTime priorityDate;

    @Column(name = Constants.VERSION_PRIORITY_DATE_ORIGIN)
    private String priorityDateOrigin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(
            column = @JoinColumn(
                name = Constants.VERSION_PRIORITY_DATE_ORIGIN,
                referencedColumnName = Constants.LOW_VALUE,
                insertable = false,
                updatable = false,
                nullable = false
            )
        ),
        @JoinColumnOrFormula(
            formula = @JoinFormula(
                value = "'" + Constants.ELEMENT_ORIGIN_DOMAIN + "'",
                referencedColumnName = Constants.DOMAIN
            )
        )
    })
    private Reference priorityDateOriginReference;

    @Column(name = Constants.VERSION_ENFORCEABLE_PRIORITY_DATE)
    public LocalDateTime enforceablePriorityDate;

    @Column(name = Constants.VERSION_STANDARDS_APPLIED)
    public String standardsApplied;

    @Column(name = Constants.VERSION_FLOW_RATE_ORIGIN)
    public String flowRateOrigin;

    @Column(name = Constants.VERSION_FLOW_RATE_DESCRIPTION)
    public String flowRateDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.VERSION_FLOW_RATE_ORIGIN, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula = @JoinFormula(value = "'" + Constants.ORIGIN_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    public Reference flowRateOriginReference;

    @Column(name = Constants.VERSION_VOLUME_ORIGIN)
    public String volumeOrigin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(
            column = @JoinColumn(
                name = Constants.VERSION_VOLUME_ORIGIN,
                referencedColumnName = Constants.LOW_VALUE,
                insertable = false,
                updatable = false,
                nullable = false
            )
        ),
        @JoinColumnOrFormula(
            formula = @JoinFormula(
                value = "'" + Constants.ELEMENT_ORIGIN_DOMAIN + "'",
                referencedColumnName = Constants.DOMAIN
            )
        )
    })
    private Reference volumeOriginReference;

    @Column(name = Constants.VERSION_ACRES_ORIGIN)
    public String acresOrigin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(
            column = @JoinColumn(
                name = Constants.VERSION_ACRES_ORIGIN,
                referencedColumnName = Constants.LOW_VALUE,
                insertable = false,
                updatable = false,
                nullable = false
            )
        ),
        @JoinColumnOrFormula(
            formula = @JoinFormula(
                value = "'" + Constants.ELEMENT_ORIGIN_DOMAIN + "'",
                referencedColumnName = Constants.DOMAIN
            )
        )
    })
    private Reference acresOriginReference;

    @OneToMany(mappedBy = "version")
    private List<DecreeVersion> decreeXrefs;

    @OneToMany(mappedBy = "version")
    private List<PointOfDiversion> pointOfDiversions;

    @Column(name = Constants.VERSION_ADJUDICATION_PROCESS)
    public String adjudicationProcess;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(
            column = @JoinColumn(
                name = Constants.VERSION_ADJUDICATION_PROCESS,
                referencedColumnName = Constants.LOW_VALUE,
                insertable = false,
                updatable = false,
                nullable = false
            )
        ),
        @JoinColumnOrFormula(
            formula = @JoinFormula(
                value = "'" + Constants.ADJUDICATION_PROCESS_DOMAIN + "'",
                referencedColumnName = Constants.DOMAIN
            )
        )
    })
    private Reference adjudicationProcessReference;

    @Column(name = Constants.VERSION_CHANGE_AUTHORIZATION_FLOW_RATE)
    public Double changeAuthorizationFlowRate;

    @Column(name = Constants.VERSION_CHANGE_AUTHORIZATION_FLOW_UNIT)
    public String changeAuthorizationFlowUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(
            column = @JoinColumn(
                name = Constants.VERSION_CHANGE_AUTHORIZATION_FLOW_UNIT,
                referencedColumnName = Constants.LOW_VALUE,
                insertable = false,
                updatable = false,
                nullable = false
            )
        ),
        @JoinColumnOrFormula(
            formula = @JoinFormula(
                value = "'" + Constants.FLOW_RATE_FULL_UNIT_DOMAIN + "'",
                referencedColumnName = Constants.DOMAIN
            )
        )
    })
    private Reference changeAuthorizationFlowUnitReference;

    @Column(name = Constants.VERSION_CHANGE_AUTHORIZATION_DIVERTED_VOLUME)
    public Double changeAuthorizationDivertedVolume;

    @Column(name = Constants.VERSION_CHANGE_AUTHORIZATION_CONSUMPTIVE_VOLUME)
    public Double changeAuthorizationConsumptiveVolume;

    @Column(name = Constants.VERSION_DATE_RECEIVED)
    public LocalDate dateReceived;

    @Column(name = Constants.VERSION_LATE_DESIGNATION)
    public String lateDesignation;

    @Column(name = Constants.VERSION_FEE_RECEIVED)
    @Convert(converter = BooleanConverter.class)
    public Boolean feeReceived;

    @Column(name = Constants.VERSION_IMPLIED_CLAIM)
    @Convert(converter = BooleanConverter.class)
    public Boolean impliedClaim;

    @Column(name = Constants.VERSION_EXEMPT_CLAIM)
    @Convert(converter = BooleanConverter.class)
    public Boolean exemptClaim;

    @Column(name = Constants.COUNTIES_ID)
    public Long countyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = Constants.COUNTIES_ID,
        referencedColumnName = Constants.COUNTIES_ID,
        nullable = false,
        updatable = false,
        insertable = false
    )
    public County county;

    @Column(name = Constants.VERSION_CASE_NUMBER)
    public String historicalCaseNumber;

    @Column(name = Constants.VERSION_FILING_DATE)
    public LocalDate historicalFilingDate;

    @Column(name = Constants.VERSION_RIGHT_TYPE)
    public String historicalRightType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(
            column = @JoinColumn(
                name = Constants.VERSION_RIGHT_TYPE,
                referencedColumnName = Constants.LOW_VALUE,
                insertable = false,
                updatable = false,
                nullable = false
            )
        ),
        @JoinColumnOrFormula(
            formula = @JoinFormula(
                value = "'" + Constants.HISTORICAL_RIGHT_TYPE_DOMAIN + "'",
                referencedColumnName = Constants.DOMAIN
            )
        )
    })
    private Reference historicalRightTypeReference;

    @Column(name = Constants.VERSION_RIGHT_TYPE_ORIGIN)
    public String historicalRightTypeOrigin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(
            column = @JoinColumn(
                name = Constants.VERSION_RIGHT_TYPE_ORIGIN,
                referencedColumnName = Constants.LOW_VALUE,
                insertable = false,
                updatable = false,
                nullable = false
            )
        ),
        @JoinColumnOrFormula(
            formula = @JoinFormula(
                value = "'" + Constants.ELEMENT_ORIGIN_DOMAIN + "'",
                referencedColumnName = Constants.DOMAIN
            )
        )
    })
    private Reference historicalRightTypeOriginReference;

    @Column(name = Constants.VERSION_DECREE_APPROPRIATOR)
    public String historicalDecreeAppropriator;

    @Column(name = Constants.VERSION_SOURCE)
    public String historicalSource;

    @Column(name = Constants.VERSION_DECREE_MONTH)
    public Integer historicalDecreeMonth;

    @Column(name = Constants.VERSION_DECREE_DAY)
    public Integer historicalDecreeDay;

    @Column(name = Constants.VERSION_DECREE_YEAR)
    public Integer historicalDecreeYear;

    @Column(name = Constants.VERSION_MINERS_INCHES)
    public Double historicalMinersInches;

    @Column(name = Constants.VERSION_FLOW_DESCRIPTION)
    public String historicalFlowDescription;

    @OneToMany(mappedBy = "waterRightVersion")
    public List<VersionCompact> versionCompacts;

}
