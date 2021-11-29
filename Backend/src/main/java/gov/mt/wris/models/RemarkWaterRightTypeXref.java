package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.RemarkWaterRightTypeXrefId;
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
@Table(name = Constants.REMARK_WATER_RIGHT_TYPE_XREF_TABLE)
@IdClass(RemarkWaterRightTypeXrefId.class)
@Getter
@Setter
public class RemarkWaterRightTypeXref {

    @Id
    @Column(name = Constants.WATER_RIGHT_TYPE_CODE)
    private String code;

    @Id
    @Column(name = Constants.REMARK_CODE)
    private String remarkCode;

    @ManyToOne(targetEntity = RemarkCode.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.REMARK_CODE, referencedColumnName = Constants.REMARK_CODE, insertable = false, updatable = false, nullable = false)
    private RemarkCode remarkCodeReference;

}
