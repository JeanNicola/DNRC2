package gov.mt.wris.models;

import java.math.BigDecimal;

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

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import gov.mt.wris.constants.Constants;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = Constants.RESERVOIR_TABLE)
public class Reservoir {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "res_seq"
    )
    @SequenceGenerator(
        name = "res_seq",
        sequenceName = Constants.RESERVOIR_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.RESERVOIR_ID)
    public BigDecimal id;

    @Column(name = Constants.WATER_RIGHT_ID)
    public BigDecimal waterRightId;

    @Column(name = Constants.VERSION_ID)
    public BigDecimal version;

    @Column(name = Constants.RESERVOIR_NAME)
    public String name;

    @Column(name = Constants.RESERVOIR_TYPE)
    public String typeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.RESERVOIR_TYPE, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula = @JoinFormula(value = "'" + Constants.RESERVOIR_TYPE_DOMAIN + "'", referencedColumnName = Constants.DOMAIN)),
    })
    public Reference typeReference;

    @Column(name = Constants.POINT_OF_DIVERSION_ID)
    public BigDecimal pointOfDiversionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.POINT_OF_DIVERSION_ID, referencedColumnName = Constants.POINT_OF_DIVERSION_ID, insertable = false, updatable = false, nullable = false)
    public PointOfDiversion pointOfDiversion;

    @Column(name = Constants.RESERVOIR_CURRENT_CAPACITY)
    public BigDecimal currentCapacity;

    @Column(name = Constants.RESERVOIR_ENLARGED_CAPACITY)
    public BigDecimal enlargedCapacity;

    @Column(name = Constants.RESERVOIR_DEPTH)
    public BigDecimal depth;

    @Column(name = Constants.RESERVOIR_HEIGHT)
    public BigDecimal height;

    @Column(name = Constants.RESERVOIR_SURFACE_AREA)
    public BigDecimal surfaceArea;

    @Column(name = Constants.RESERVOIR_ELEVATION)
    public BigDecimal elevation;

    @Column(name = Constants.RESERVOIR_ORIGIN)
    public String origin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
        @JoinColumnOrFormula(column = @JoinColumn(name = Constants.RESERVOIR_ORIGIN, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
        @JoinColumnOrFormula(formula = @JoinFormula(value = "'" + Constants.RESERVOIR_ORIGIN_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    public Reference originReference;

    @Column(name = Constants.RESERVOIR_CHANGED)
    public String changed;

    @Column(name = Constants.LEGAL_LAND_DESCRIPTION_ID)
    public BigDecimal legalLandDescriptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.LEGAL_LAND_DESCRIPTION_ID, referencedColumnName = Constants.LEGAL_LAND_DESCRIPTION_ID, insertable = false, updatable = false, nullable = false)
    public LegalLandDescription legalLandDescription;

    @Column(name = Constants.COUNTIES_ID)
    public BigDecimal countyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.COUNTIES_ID, referencedColumnName = Constants.COUNTIES_ID, insertable = false, updatable = false, nullable = false)
    public County county;
}
