package gov.mt.wris.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.PointOfDiversionEnforcementId;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.POINT_OF_DIVERSION_ENFORCEMENT_XREF_TABLE)
@IdClass(PointOfDiversionEnforcementId.class)
@Getter
@Setter
public class PointOfDiversionEnforcement {
    @Id
    @Column(name = Constants.POINT_OF_DIVERSION_ID)
    public BigDecimal pointOfDiversionId;

    @Id
    @Column(name = Constants.ENFORCEMENT_AREA_ID)
    public String enforcementId;

    @Id
    @Column(name = Constants.POINT_OF_DIVERSION_ENFORCEMENT_NUMBER)
    public String enforcementNumber;

    @Column(name = Constants.POINT_OF_DIVERSION_ENFORCEMENT_COMMENTS)
    public String comments;

    @ManyToOne(targetEntity = PointOfDiversion.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.POINT_OF_DIVERSION_ID, referencedColumnName = Constants.POINT_OF_DIVERSION_ID, insertable = false, updatable = false, nullable = false)
    public PointOfDiversion pointOfDiversion;

    @ManyToOne(targetEntity = EnforcementArea.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.ENFORCEMENT_AREA_ID, referencedColumnName = Constants.ENFORCEMENT_AREA_ID, insertable = false, updatable = false, nullable = false)
    public EnforcementArea enforcementArea;
}
