package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.RelatedRightVersionId;
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
@Table(name= Constants.WRD_RELATED_RIGHT_VERS_XREFS)
@IdClass(RelatedRightVersionId.class)
@Getter
@Setter
public class RelatedRightVerXref {

    @Id
    @Column(name = Constants.RELATED_RIGHT_ID)
    public BigDecimal relatedRightId;

    @Id
    @Column(name = Constants.WATER_RIGHT_ID)
    public BigDecimal waterRightId;

    @Id
    @Column(name = Constants.VERSIONS_ID)
    public BigDecimal versionId;

    @ManyToOne(targetEntity = RelatedRight.class)
    @JoinColumn(name = Constants.RELATED_RIGHT_ID, referencedColumnName = Constants.RELATED_RIGHT_ID, updatable = false, nullable = false, insertable = false)
    public RelatedRight relatedRight;

    @ManyToOne(targetEntity = WaterRight.class)
    @JoinColumn(name = Constants.WATER_RIGHT_ID, referencedColumnName = Constants.WATER_RIGHT_ID, updatable = false, nullable = false, insertable = false)
    public WaterRight waterRight;

    @ManyToOne(targetEntity = WaterRightVersion.class, fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = Constants.VERSION_ID, referencedColumnName = Constants.VERSION_ID,
                    insertable=false, updatable=false),
            @JoinColumn(name = Constants.WATER_RIGHT_ID, referencedColumnName = Constants.WATER_RIGHT_ID,
                    insertable=false, updatable=false)
    })
    public WaterRightVersion version;

}
