package gov.mt.wris.models;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.POINT_OF_DIVERSION_TABLE)
@Getter
@Setter
public class PointOfDiversion {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "pod_sequence"
    )
    @SequenceGenerator(
        name = "pod_sequence",
        sequenceName = Constants.POINT_OF_DIVERSION_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.POINT_OF_DIVERSION_ID)
    public BigDecimal id;

    @Column(name = Constants.POINT_OF_DIVERSION_NUMBER)
    public BigDecimal number;

    @Column(name = Constants.POINT_OF_DIVERSION_TYPE_CODE)
    public String typeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.POINT_OF_DIVERSION_TYPE_CODE, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula = @JoinFormula(value = "'" + Constants.POINT_OF_DIVERSION_TYPE_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    public Reference typeReference;

    @Column(name = Constants.MEANS_OF_DIVERSION_CODE)
    public String meansCode;

    @ManyToOne(targetEntity = MeansOfDiversion.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.MEANS_OF_DIVERSION_CODE, insertable = false, updatable = false, nullable = false)
    public MeansOfDiversion meansOfDiversion;

    @Column(name = Constants.MAJOR_TYPE)
    public String majorTypeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.MAJOR_TYPE, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula = @JoinFormula(value = "'" + Constants.MAJOR_TYPE_DOMAIN+ "'", referencedColumnName = Constants.DOMAIN))
    })
    public Reference majorTypeReference;

    @Column(name = Constants.MINOR_TYPE_CODE)
    public String minorTypeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.MINOR_TYPE_CODE, insertable = false, updatable = false, nullable = false)
    public MinorType minorType;

    @Column(name = Constants.WATER_RIGHT_ID)
    public BigDecimal waterRightId;

    @Column(name = Constants.VERSION_ID)
    public BigDecimal versionId;

    @ManyToOne(targetEntity = WaterRightVersion.class, fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = Constants.WATER_RIGHT_ID, referencedColumnName = Constants.WATER_RIGHT_ID, insertable = false, updatable = false, nullable = false),
        @JoinColumn(name = Constants.VERSIONS_ID, referencedColumnName = Constants.VERSIONS_ID, insertable = false, updatable = false, nullable = false)
    })
    public WaterRightVersion version;

    @Column(name = Constants.POINT_OF_DIVERSION_COUNTY_ID)
    public BigDecimal countyId;

    @ManyToOne(targetEntity = County.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.POINT_OF_DIVERSION_COUNTY_ID, insertable = false, updatable = false, nullable = false)
    public County county;

    @Column(name = Constants.LEGAL_LAND_DESCRIPTION_ID)
    public BigDecimal legalLandDescriptionId;

    @ManyToOne(targetEntity = LegalLandDescription.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.LEGAL_LAND_DESCRIPTION_ID, insertable = false, updatable = false, nullable = false)
    public LegalLandDescription legalLandDescription;

    @Column(name = Constants.DITCH_ID)
    public BigDecimal ditchId;

    @ManyToOne(targetEntity = Ditch.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.DITCH_ID, insertable = false, updatable = false, nullable = false)
    public Ditch ditch;

    @Column(name = Constants.POINT_OF_DIVERSION_ORIGIN_CODE)
    public String originCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.POINT_OF_DIVERSION_ORIGIN_CODE, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula = @JoinFormula(value = "'" + Constants.POINT_OF_DIVERSION_ORIGIN_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    public Reference originReference;

    @Column(name = Constants.POINT_OF_DIVERSION_MODIFIED)
    public String modified;

    @Column(name = Constants.POINT_OF_DIVERSION_X_COORDINATE)
    public BigDecimal xCoordinate;

    @Column(name = Constants.POINT_OF_DIVERSION_Y_COORDINATE)
    public BigDecimal yCoordinate;

    @Column(name = Constants.POINT_OF_DIVERSION_SOURCE_ORIGIN_CODE)
    public String sourceOriginCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.POINT_OF_DIVERSION_SOURCE_ORIGIN_CODE, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula = @JoinFormula(value = "'" + Constants.POINT_OF_DIVERSION_SOURCE_ORIGIN_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    public Reference sourceOriginReference;

    @Column(name = Constants.POINT_OF_DIVERSION_UNNAMED)
    public String unnamedTributary;

    @Column(name = Constants.SOURCE_ID)
    public BigDecimal sourceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.SOURCE_ID, insertable = false, updatable = false, nullable = false)
    public Source source;

    @Column(name = Constants.SUBDIVISION_CODES_CODE)
    public String subdivisionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.SUBDIVISION_CODES_CODE, insertable = false, updatable = false, nullable = false)
    public SubdivisionCode subdivision;

    @Column(name = Constants.POINT_OF_DIVERSION_TRANSITORY)
    public String transitory;

    @Column(name = Constants.POINT_OF_DIVERSION_LOT)
    public String lot;

    @Column(name = Constants.POINT_OF_DIVERSION_BLOCK)
    public String block;

    @Column(name = Constants.POINT_OF_DIVERSION_TRACT)
    public String tract;

    @Column(name = Constants.POINT_OF_DIVERSION_WELL_DEPTH)
    public Double wellDepth;

    @Column(name = Constants.POINT_OF_DIVERSION_WATER_LEVEL)
    public Double waterLevel;

    @Column(name = Constants.POINT_OF_DIVERSION_CASTING_DIAMETER)
    public Double castingDiameter;

    @Column(name = Constants.POINT_OF_DIVERSION_FLOWING)
    public String flowing;

    @Column(name = Constants.POINT_OF_DIVERSION_PUMP_SIZE)
    public Double pumpSize;

    @Column(name = Constants.POINT_OF_DIVERSION_WATER_TEMP)
    public Double waterTemp;

    @Column(name = Constants.POINT_OF_DIVERSION_TEST_RATE)
    public Double testRate;

    @OneToMany(mappedBy = "pointOfDiversion", fetch = FetchType.LAZY)
    public List<Address> addresses;

    // Columns added to support the Copy POD functionality
    @Column(name = Constants.ENFORCEMENT_AREA_ID)
    public String enforcementAreaId;

    @Column(name = Constants.POINT_OF_DIVERSION_ENFORCEMENT_COMMENTS)
    public String enforcementComment;

    @Column(name = Constants.POINT_OF_DIVERSION_ENFORCEMENT_NUMBER)
    public String enforcementNumber;

    @Column(name = Constants.POINT_OF_DIVERSION_MODIFIED_ELEM_ORGN)
    public String modifiedElementOrigin;
    
    @Column(name = Constants.POINT_OF_DIVERSION_PERCENTAGE_OF_REACH)
    public Integer percentageOfReach;
    
    @Column(name = Constants.POINT_OF_DIVERSION_PODV_ID_AFT)
    public BigDecimal podIdAft;

    @Column(name = Constants.POINT_OF_DIVERSION_PRCS_STS)
    public String prcsSts;
    
    @Column(name = Constants.POINT_OF_DIVERSION_PRE_MJR)
    public String preMjr;
    
    @Column(name = Constants.POINT_OF_DIVERSION_PRE_MRTP)
    public String preMrtp;
    
    @Column(name = Constants.POINT_OF_DIVERSION_PRE_SOURCE_ID)
    public BigDecimal preSourIdSeq;
    
    @Column(name = Constants.POINT_OF_DIVERSION_PRE_UT)
    public String preUt;
    
    @Column(name = Constants.POINT_OF_DIVERSION_REAC_ID_SEQ)
    public BigDecimal reacIdSeq;
    
    @Column(name = Constants.POINT_OF_DIVERSION_SVTP_ID_SEQ)
    public BigDecimal svtpIdSeq;
    
    @Column(name = Constants.POINT_OF_DIVERSION_WRKEY)
    public String wrKey;
}