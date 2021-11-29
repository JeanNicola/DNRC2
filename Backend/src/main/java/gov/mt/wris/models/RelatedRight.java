package gov.mt.wris.models;


import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = Constants.RELATED_RIGHT_TABLE)
@Getter
@Setter
public class RelatedRight {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "rlrt_seq"
    )
    @SequenceGenerator(
            name = "rlrt_seq",
            sequenceName = Constants.RELATED_RIGHT_SEQUENCE,
            allocationSize = 1
    )
    @Column(name = Constants.RELATED_RIGHT_ID)
    public BigDecimal relatedRightId;

    @Column(name = Constants.RELATED_RIGHT_TYPE)
    public String relationshipType;
    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.RELATED_RIGHT_TYPE, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.RELATED_RIGHT_TYPE_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference relationshipTypeVal;

    @ManyToMany
    @JoinTable(
            name = Constants.WRD_RELATED_RIGHT_VERS_XREFS,
            joinColumns = { @JoinColumn(name = Constants.RELATED_RIGHT_ID) },
            inverseJoinColumns = { @JoinColumn(name = Constants.WATER_RIGHT_ID) }
    )
    public Set<WaterRight> waterRight;

    @OneToMany(mappedBy = "relatedRight", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<SharedElement> sharedElement;

    @OneToMany(mappedBy = "relatedRight", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<RelatedRightVerXref> relatedRightVerXref;

    @Column(name = Constants.RELATED_RIGHT_CREATED_BY)
    public String createdBy;

    @Column(name = Constants.RELATED_RIGHT_CREATED_DATE)
    public LocalDate createdDate;

    @Column(name = Constants.RELATED_RIGHT_MODIFIED_BY)
    public String modifiedBy;

    @Column(name = Constants.RELATED_RIGHT_MODIFIED_DATE)
    public LocalDate modifiedDate;

    @Column(name = Constants.RELATED_RIGHT_MAX_FLOW_RATE)
    public Long maxFlowRate;

    @Column(name = Constants.RELATED_RIGHT_FLOW_RATE_UNIT)
    public String flowRateUnit;

    @ManyToOne(targetEntity = Reference.class, fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = Constants.RELATED_RIGHT_FLOW_RATE_UNIT, referencedColumnName = Constants.LOW_VALUE, insertable = false, updatable = false, nullable = false)),
            @JoinColumnOrFormula(formula=@JoinFormula(value = "'" + Constants.FLOW_RATE_UNIT_DOMAIN + "'", referencedColumnName = Constants.DOMAIN))
    })
    private Reference flowRateUnitVal;

    @Column(name = Constants.RELATED_RIGHT_MAX_ACRES)
    public Double maxAcres;

    @Column(name = Constants.RELATED_RIGHT_MAX_VOLUME)
    public Double maxVolume;


}
