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

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.DITCH_TABLE)
@Getter
@Setter
public class Ditch {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "ditch_seq"
    )
    @SequenceGenerator(
        name = "ditch_seq",
        sequenceName = Constants.DITCH_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.DITCH_ID)
    public BigDecimal id;

    @Column(name = Constants.DITCH_NAME)
    public String name;
    
    @Column(name = Constants.DIVERSION_TYPE_CODE)
    public String diversionTypeCode;

    @ManyToOne(targetEntity = DiversionType.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.DIVERSION_TYPE_CODE, insertable = false, updatable = false, nullable = false)
    public DiversionType diversionType;

    @Column(name = Constants.LEGAL_LAND_DESCRIPTION_ID)
    public BigDecimal legalLandDescriptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.LEGAL_LAND_DESCRIPTION_ID, insertable = false, updatable = false, nullable = false)
    public LegalLandDescription legalLandDescription;

    @Column(name = Constants.COUNTIES_ID)
    public BigDecimal countyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.COUNTIES_ID, insertable = false, updatable = false, nullable = false)
    public County county;

    @Column(name = Constants.DITCH_CAPACITY)
    public Double capacity;

    @Column(name = Constants.DITCH_DEPTH)
    public Double depth;

    @Column(name = Constants.DITCH_WIDTH)
    public Double width;

    @Column(name = Constants.DITCH_LENGTH)
    public Double length;

    @Column(name = Constants.DITCH_SLOPE)
    public Double slope;

    @Column(name = Constants.DITCH_VALID)
    public String valid;
}
