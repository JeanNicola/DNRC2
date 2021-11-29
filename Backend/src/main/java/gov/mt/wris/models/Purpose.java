package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = Constants.PURPOSES_TABLE)
@Getter
@Setter
public class Purpose {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "purpose_seq"
    )
    @SequenceGenerator(
            name = "purpose_seq",
            sequenceName = Constants.PURPOSE_SEQUENCE,
            allocationSize = 1
    )
    @Column(name = Constants.PURPOSE_ID)
    BigDecimal purposeId;

    @OneToMany(targetEntity = PurposeIrrigationXref.class, fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE})
    @JoinColumn(name = Constants.PURPOSE_ID, referencedColumnName = Constants.PURPOSE_ID, nullable = false, updatable = false, insertable = false)
    public List<PurposeIrrigationXref> purposeIrrigationXrefs;

    @Column(name = Constants.VERSION_ID)
    BigDecimal versionId;

    @Column(name = Constants.WATER_RIGHT_ID)
    BigDecimal waterRightId;

    @ManyToOne(targetEntity = WaterRightVersion.class, fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = Constants.VERSION_ID, referencedColumnName = Constants.VERSION_ID, insertable = false, updatable = false, nullable = false),
            @JoinColumn(name = Constants.WATER_RIGHT_ID, referencedColumnName = Constants.WATER_RIGHT_ID, insertable = false, updatable = false, nullable = false)
    })
    public WaterRightVersion waterRightVersion;

    @Column(name = Constants.PURT_CODE)
    String purposeTypeCode;

    @ManyToOne(targetEntity = PurposeType.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.PURT_CODE, referencedColumnName = Constants.PURT_CODE, nullable = false, updatable = false, insertable = false)
    public PurposeType purposeType;

    @Column(name = Constants.ELEMENT_ORIGIN)
    String elementOrigin;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.ELEMENT_ORIGIN, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.OWNER_ORIGIN_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    public Reference elementOriginReference;

    @Column(name = Constants.VOLUME_AMOUNT)
    private BigDecimal volumeAmount;

    @Column(name = Constants.VOLUME_DESCRIPTION)
    private String volumeDescription;

    @Column(name = Constants.ANIMAL_UNITS)
    private BigDecimal animalUnits;

    @Column(name = Constants.PURPOSE_CLARIFICATION)
    private String purposeClarification;

    @Column(name = Constants.HOUSEHOLD)
    private Integer household;

    @Column(name = Constants.CLIMATIC_AREA_CODE)
    private String climaticAreaCode;

    @ManyToOne(targetEntity = ClimaticArea.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.CLIMATIC_AREA_CODE, referencedColumnName = Constants.CLIMATIC_AREA_CODE, nullable = false, updatable = false, insertable = false)
    public ClimaticArea climaticArea;

    @Column(name = Constants.CROP_ROTATION)
    private String cropRotation;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.CROP_ROTATION, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.YES_NO_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    public Reference cropRotationReference;

    @Column(name = Constants.MODIFIED_IN_VERSION)
    private String modified;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.MODIFIED_IN_VERSION, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.YES_NO_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    public Reference modifiedReference;

    @OneToMany(mappedBy = "purpose", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<PeriodOfUse> periods;

    @OneToMany(mappedBy = "purpose", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<RetiredPlaceOfUse> retiredPlacesOfUses;

    @OneToMany(mappedBy = "purpose", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<PlaceOfUse> places;

    @OneToMany(mappedBy = "purpose", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<Examination> examinations;

}
