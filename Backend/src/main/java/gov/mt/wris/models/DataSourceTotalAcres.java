package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = Constants.POU_EXAMINATIONS_TABLE)
@Getter
@Setter
public class DataSourceTotalAcres {

    @Id
    @Column(name = Constants.POU_EXAMINATION_ID)
    public BigDecimal pexmId;

    @Formula(value = "(SELECT SUM(T.ACRES) \n" +
            "   FROM WRD_POU_EXAMINATION_POU_XREFS T \n" +
            "   WHERE  T.PEXM_ID_SEQ = PEXM_ID_SEQ) \n")
    public BigDecimal totalAcres;

    @OneToOne(mappedBy = "dataSourceTotalAcres", fetch = FetchType.LAZY, optional = false)
    public PouExamination pouExamination;

}
