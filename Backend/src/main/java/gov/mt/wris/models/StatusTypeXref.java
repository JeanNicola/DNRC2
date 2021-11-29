package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.StatusTypeXrefId;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = Constants.STATUS_TYPE_XREF_TABLE)
@IdClass(StatusTypeXrefId.class)
@Getter
@Setter
public class StatusTypeXref {
    @Id
    @Column(name = Constants.WATER_RIGHT_TYPE_CODE)
    public String typeCode;

    @Id
    @Column(name = Constants.WATER_RIGHT_STATUS_CODE)
    public String statusCode;

    @Column(name = Constants.LOV_ITEM)
    public String lovItem;
    
    @ManyToOne(targetEntity = WaterRightStatus.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.WATER_RIGHT_STATUS_CODE, referencedColumnName = Constants.WATER_RIGHT_STATUS_CODE, nullable = false, insertable = false, updatable = false)
    public WaterRightStatus status;
}
