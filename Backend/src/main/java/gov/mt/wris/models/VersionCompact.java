package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = Constants.VERSION_COMPACT_TABLE)
@Getter
@Setter
public class VersionCompact {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "version_compact_sequence"
    )
    @SequenceGenerator(
        name = "version_compact_sequence",
        sequenceName = Constants.VERSION_COMPACT_SEQUENCE,
        allocationSize = 1
    )
    @Column(name = Constants.VERSION_COMPACT_ID)
    public BigDecimal id;

    @Column(name = Constants.SUBCOMPACT_ID)
    public BigDecimal subcompactId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.SUBCOMPACT_ID, referencedColumnName = Constants.SUBCOMPACT_ID, insertable = false, updatable = false, nullable = false)
    public Subcompact subcompact;

    @Column(name = Constants.VERSION_COMPACT_EXEMPT)
    public String exempt;

    @Column(name = Constants.VERSION_COMPACT_AFFECTS)
    public String affects;

    @Column(name = Constants.VERSION_COMPACT_TRANSBASIN)
    public String transbasin;

    @Column(name = Constants.WATER_RIGHT_ID)
    public BigDecimal waterRightId;

    @Column(name = Constants.VERSION_ID)
    public BigDecimal versionId;

    @ManyToOne(targetEntity = WaterRightVersion.class)
    @JoinColumns({
            @JoinColumn(name = Constants.WATER_RIGHT_ID, referencedColumnName = Constants.WATER_RIGHT_ID, nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = Constants.VERSION_ID, referencedColumnName = Constants.VERSION_ID, nullable = false, insertable = false, updatable = false) })
    public WaterRightVersion waterRightVersion;
}
