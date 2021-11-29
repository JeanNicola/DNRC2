package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.RetiredPlaceOfUseId;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = Constants.RETIRED_PLACE_OF_USE_TABLE)
@IdClass(RetiredPlaceOfUseId.class)
@Getter
@Setter
public class RetiredPlaceOfUse extends PlaceOfUseSharedProps {

    @Id
    @Column(name = Constants.RETIRED_PLACE_OF_USE_ID)
    private BigDecimal retiredPlaceId;

    @Id
    @Column(name = Constants.PURPOSE_ID)
    private BigDecimal purposeId;

    @Column(name = Constants.PLACE_OF_USE_ACREAGE)
    private BigDecimal acreage;

    @Column(name = Constants.ELEMENT_ORIGIN)
    private String elementOrigin;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.ELEMENT_ORIGIN, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.OWNER_ORIGIN_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference elementOriginReference;

    @Column(name = Constants.LEGAL_LAND_DESCRIPTION_ID)
    private BigDecimal legalLandDescriptionId;

    @ManyToOne(targetEntity = LegalLandDescription.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.LEGAL_LAND_DESCRIPTION_ID, insertable = false, updatable = false, nullable = false)
    public LegalLandDescription legalLandDescription;

    @Column(name = Constants.MODIFIED_IN_VERSION)
    private String modified;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.MODIFIED_IN_VERSION, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.YES_NO_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference modifiedReference;

    @Column(name = Constants.COUNTIES_ID)
    private BigDecimal countyId;

    @ManyToOne(targetEntity = Purpose.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.PURPOSE_ID, insertable = false, updatable = false, nullable = false)
    public Purpose purpose;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.COUNTIES_ID, referencedColumnName = Constants.COUNTIES_ID, insertable = false, updatable = false, nullable = false)
    public County county;

    @OneToMany(mappedBy = "retiredPlaceOfUse", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<RetiredPouSubdivisionXref> subdivisions;


}
