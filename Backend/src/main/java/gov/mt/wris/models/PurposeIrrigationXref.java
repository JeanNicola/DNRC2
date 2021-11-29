package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.PurposeIrrigationXrefId;
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
import java.math.BigDecimal;

@Entity
@Table(name = Constants.PURPOSE_IRRIGATION_XREF_TABLE)
@IdClass(PurposeIrrigationXrefId.class)
@Getter
@Setter
public class PurposeIrrigationXref {

    @Id
    @Column(name = Constants.PURPOSE_ID)
    public BigDecimal purposeId;

    @Id
    @Column(name = Constants.IRRIGATION_TYPE_CODE)
    public String irrigationTypeCode;

    @ManyToOne(targetEntity = IrrigationType.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.IRRIGATION_TYPE_CODE, referencedColumnName = Constants.IRRIGATION_TYPE_CODE, nullable = false, insertable = false, updatable = false)
    public IrrigationType irrigationType;

}
