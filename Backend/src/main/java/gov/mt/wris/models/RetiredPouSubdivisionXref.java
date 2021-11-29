package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.RetiredPouSubdivisionXrefId;
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
@Table(name = Constants.WRD_POU_RET_SUBDIVISION_XREFS_TABLE)
@IdClass(RetiredPouSubdivisionXrefId.class)
@Getter
@Setter
public class RetiredPouSubdivisionXref {

    @Id
    @Column(name = Constants.RETIRED_PLACE_OF_USE_ID)
    private BigDecimal retiredPlaceId;

    @Id
    @Column(name = Constants.PURPOSE_ID)
    private BigDecimal purposeId;

    @Id
    @Column(name = Constants.SUBDIVISION_CODES_CODE)
    private String code;

    @Column(name = Constants.WRD_POU_RET_SUB_BLK)
    private String blk;

    @Column(name = Constants.WRD_POU_RET_SUB_LOT)
    private String lot;

    @ManyToOne(targetEntity = RetiredPlaceOfUse.class, fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = Constants.RETIRED_PLACE_OF_USE_ID, referencedColumnName = Constants.RETIRED_PLACE_OF_USE_ID, insertable = false, updatable = false, nullable = false),
            @JoinColumn(name = Constants.PURPOSE_ID, referencedColumnName = Constants.PURPOSE_ID, insertable = false, updatable = false, nullable = false)
    })
    public RetiredPlaceOfUse retiredPlaceOfUse;

    @ManyToOne(targetEntity = SubdivisionCode.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.SUBDIVISION_CODES_CODE, insertable = false, updatable = false, nullable = false)
    public SubdivisionCode subdivisionCode;

}
