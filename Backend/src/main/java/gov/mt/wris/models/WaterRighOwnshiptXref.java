package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.WaterRighOwnshiptXrefId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name= Constants.WRD_WTR_RGT_OWNSHIP_UPDT_XREFS_TABLE)
@IdClass(WaterRighOwnshiptXrefId.class)
@Getter
@Setter
public class WaterRighOwnshiptXref {

    @Id
    @Column(name = Constants.OWNR_UPDT_ID)
    public BigDecimal ownershipUpdateId;

    @Id
    @Column(name = Constants.WATER_RIGHT_ID)
    public BigDecimal waterRightId;

    @ManyToOne(targetEntity = OwnershipUpdate.class, fetch= FetchType.LAZY)
    @JoinColumn(name = Constants.OWNR_UPDT_ID, referencedColumnName = Constants.OWNR_UPDT_ID, updatable = false, nullable = false, insertable = false)
    public OwnershipUpdate ownershipUpdate;

    @ManyToOne(targetEntity = WaterRight.class, fetch= FetchType.LAZY)
    @JoinColumn(name = Constants.WATER_RIGHT_ID, referencedColumnName = Constants.WATER_RIGHT_ID, updatable = false, nullable = false, insertable = false)
    public WaterRight waterRight;

    @Column(name= Constants.WTR_XREF_CREATED_BY)
    public String createdBy;

    @Column(name= Constants.WTR_XREF_DTM_CREATED)
    public LocalDate dateCreated;

    @Column(name= Constants.WTR_XREF_MOD_BY)
    public String modBy;

    @Column(name= Constants.WTR_XREF_DTM_MOD)
    public LocalDate dateMod;

}
