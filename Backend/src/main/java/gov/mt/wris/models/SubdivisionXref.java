package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.SubdivisionXrefId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = Constants.SUBDIVISION_XREFS_TABLE)
@IdClass(SubdivisionXrefId.class)
@Getter
@Setter
public class SubdivisionXref {

    @Id
    @Column(name = Constants.PLACE_OF_USE_ID)
    private BigDecimal placeId;

    @Id
    @Column(name = Constants.SUBDIVISION_CODES_CODE)
    private String code;

    @Id
    @Column(name = Constants.PURPOSE_ID)
    private BigDecimal purposeId;

    @ManyToOne(targetEntity = SubdivisionCode.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.SUBDIVISION_CODES_CODE, referencedColumnName = Constants.SUBDIVISION_CODES_CODE, nullable = false, insertable = false, updatable = false)
    public SubdivisionCode subdivisionCode;

    @Column(name = Constants.SUBDIVISION_BLOCK)
    private String blk;

    @Column(name = Constants.SUBDIVISION_LOT)
    private String lot;

    @ManyToOne(targetEntity = PlaceOfUse.class, fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = Constants.PLACE_OF_USE_ID, referencedColumnName = Constants.PLACE_OF_USE_ID, insertable = false, updatable = false, nullable = false),
            @JoinColumn(name = Constants.PURPOSE_ID, referencedColumnName = Constants.PURPOSE_ID, insertable = false, updatable = false, nullable = false)
    })
    public PlaceOfUse placeOfUse;

}
