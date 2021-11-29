package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import gov.mt.wris.models.IdClasses.PouExamUsgsMapId;
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
@Table(name = Constants.EXAM_USGS_MAP_XREF_TABLE)
@IdClass(PouExamUsgsMapId.class)
@Getter
@Setter
public class PouExamUsgsMapXref{

    @Id
    @Column(name = Constants.USGS_ID)
    private BigDecimal utmpId;

    @Id
    @Column(name = Constants.POU_EXAMINATION_ID)
    private BigDecimal pexmId;

    @ManyToOne(targetEntity = PouExamination.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.POU_EXAMINATION_ID, referencedColumnName = Constants.POU_EXAMINATION_ID, insertable = false, updatable = false, nullable = false)
    public PouExamination pouExamination;

    @ManyToOne(targetEntity = Usgs.class, fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.USGS_ID, referencedColumnName = Constants.USGS_ID, insertable = false, updatable = false, nullable = false)
    public Usgs usgs;

}
